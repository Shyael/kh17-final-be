package com.kh.khedu.websocket.event;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.kh.khedu.service.FlashService;
import com.kh.khedu.service.JwtService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.websocket.vo.WebSocketV3SystemVO;
import com.kh.khedu.websocket.vo.WebSocketV4SystemVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebSocketEventHandler {
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private FlashService flashService;
	
	//지정된 이벤트 상황이 발생하면 해당 메소드가 자동으로 실행되도록 설정
	@EventListener
	public void enter(SessionConnectedEvent event) {//입장 이벤트
		connectV3(event);//version 3 처리
		connectV4(event);//version 4 처리
	}
	@EventListener
	public void leave(SessionDisconnectEvent event) {//퇴장 이벤트
		disconnectV3(event);
		disconnectV4(event);
	}
	@EventListener
	public void subscribe(SessionSubscribeEvent event) {//구독 이벤트
		subscribeV3(event);
		subscribeV4(event);
	}
	
	//version 3 처리
	private void connectV3(SessionConnectedEvent event) {
		log.debug("사용자 접속!");
		
		//사용자 정보 추출 (자동화를 쓰기 어려우며 직접 변환)
		Principal principal = event.getUser();//인증정보 추출(추상화)
		if(!(principal instanceof JwtAuthenticationToken)) return;
		
		JwtAuthenticationToken auth = (JwtAuthenticationToken)principal;
		Jwt jwt = auth.getToken();
		TokenParseResponseVO parseVO = jwtService.parseAccessToken(jwt);
		
		//사용자 정보를 저장(인원수 계산)
		flashService.enter(parseVO);
		simpMessagingTemplate.convertAndSend("/public/users", flashService.list());
		
		WebSocketV3SystemVO response = WebSocketV3SystemVO.builder()
					//.content("["+parseVO.getAccountNickname()+"] 님이 입장하셨습니다")
					.content("["+parseVO.getAccountId()+"] 님이 입장하셨습니다")
					.level("primary")
					.time(LocalDateTime.now())
				.build();
		
		simpMessagingTemplate.convertAndSend("/public/system", response);
	}
	private void disconnectV3(SessionDisconnectEvent event) {//퇴장 이벤트
		//사용자 정보 추출 (자동화를 쓰기 어려우며 직접 변환)
		Principal principal = event.getUser();//인증정보 추출(추상화)
		if(!(principal instanceof JwtAuthenticationToken)) return;
		
		JwtAuthenticationToken auth = (JwtAuthenticationToken)principal;
		Jwt jwt = auth.getToken();
		TokenParseResponseVO parseVO = jwtService.parseAccessToken(jwt);
		
		//사용자 정보를 저장(인원수 계산)
		flashService.leave(parseVO);
		simpMessagingTemplate.convertAndSend("/public/users", flashService.list());
		
		WebSocketV3SystemVO response = WebSocketV3SystemVO.builder()
				//.content("["+parseVO.getAccountNickname()+"] 님이 퇴장하셨습니다")
				.content("["+parseVO.getAccountId()+"] 님이 퇴장하셨습니다")
				.level("primary")
				.time(LocalDateTime.now())
			.build();
		
		simpMessagingTemplate.convertAndSend("/public/system", response);
	}
	private void subscribeV3(SessionSubscribeEvent event) {//구독 이벤트
		log.debug("사용자가 채널을 구독했습니다!");
		
		//구독한 채널의 이름을 알아내서 원하는 채널일 때 이벤트를 발생시키도록 처리
		//→ 채널의 이름은 destination이라고 부름
		SimpMessageHeaderAccessor headerAccessor = 
					SimpMessageHeaderAccessor.wrap(event.getMessage());
		
		//사용자 정보 추출 (자동화를 쓰기 어려우며 직접 변환)
		Principal principal = event.getUser();//인증정보 추출(추상화)
		if(!(principal instanceof JwtAuthenticationToken)) return;
		
		JwtAuthenticationToken auth = (JwtAuthenticationToken)principal;
		Jwt jwt = auth.getToken();
		TokenParseResponseVO parseVO = jwtService.parseAccessToken(jwt);
		
		String destination = headerAccessor.getDestination();
//		if(destination.equals("/public/users")) {
//			simpMessagingTemplate.convertAndSend("/public/users", flashService.list());
//		}
		if(destination.equals("/private/users/"+parseVO.getAccountId())) {
			simpMessagingTemplate.convertAndSend(
				"/private/users/"+parseVO.getAccountId(),
				flashService.list()
			);
		}
	}
	
	//방번호(DestinationVariable)를 읽을 수 있을까? 
	//→ 한번에 읽어내는 명령은 없음
	//→ 원하는 구간을 잘라내야함
	private void connectV4(SessionConnectedEvent event) {
		//여기선 할게 없음 (방에 들어오는 시점이 여기가 아님.. 여기는 Online이 되는 시점)
	}
	private void disconnectV4(SessionDisconnectEvent event) {
		//여기선 할게 없음 (방에서 나가는 시점이 여기가 아님.. 여기는 Offline이 되는 시점)
	}
	private void subscribeV4(SessionSubscribeEvent event) {
		//여기선 할게 없음
	}
}