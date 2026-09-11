package com.kh.khedu.websocket.server;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.kh.khedu.dao.MessageDao;
import com.kh.khedu.service.JwtService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.room.RoomChatMessageVO;
import com.kh.khedu.websocket.vo.WebSocketChatVO;
import com.kh.khedu.websocket.vo.WebSocketRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebSocketServer {
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private MessageDao messageDao;
	
	//메세지가 오는 채널명 : /app/방번호/chat
	@MessageMapping("/{roomNo}/chat")
	public void groupChat(@DestinationVariable int roomNo,
					@AuthenticationPrincipal Jwt jwt,
					Message<WebSocketRequestVO> message) {
		//인증정보 복원
		TokenParseResponseVO parseVO = jwtService.parseAccessToken(jwt);
		
		//메세지 해석 (헤더 + 바디)
		WebSocketRequestVO request = message.getPayload();
		
		//발신 메세지 생성1
		WebSocketChatVO response = WebSocketChatVO.builder()
					.senderNo(parseVO.getAccountNo())
					.senderName(parseVO.getAccountName())
					.senderType(parseVO.getAccountType())
					.content(request.getContent())
					.time(LocalDateTime.now())
				.build();
		
		//DB 저장시점
		int messageNo = messageDao.sequence();
		messageDao.insertChat(RoomChatMessageVO.builder()
					.messageNo(messageNo)
					.messageRoom(roomNo)
					.messageType(response.getType())
					.messageSenderNo(response.getSenderNo())
					.messageSenderType(response.getSenderType())
					.messageSenderName(response.getSenderName())
					.messageTime(Timestamp.valueOf(response.getTime()))
					.messageContent(response.getContent())
				.build());
		
		Map<String, Object> params = new HashMap<>();
		params.put("roomNo", roomNo);
		simpMessagingTemplate.convertAndSend("/public/room/check", params);
		simpMessagingTemplate.convertAndSend("/public/"+roomNo+"/chat", response);
	}
}
