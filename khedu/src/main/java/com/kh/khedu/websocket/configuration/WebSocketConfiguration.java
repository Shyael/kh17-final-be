package com.kh.khedu.websocket.configuration;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

//웹소켓 설정
//@EnableWebSocker//웹소켓을 서버에서 사용하는 것을 허용 (클래식 웹소켓)
@EnableWebSocketMessageBroker//STOMP의 사용을 허용
@Configuration
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	//연결 설정
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	    registry.addEndpoint("/ws", "/ws-member")
	            .setAllowedOriginPatterns("*")
	            // 아래 setHandshakeHandler 추가
	            .setHandshakeHandler(new DefaultHandshakeHandler() {
	                @Override
	                protected Principal determineUser(ServerHttpRequest request, 
	                                                  WebSocketHandler wsHandler, 
	                                                  Map<String, Object> attributes) {
	                    return SecurityContextHolder.getContext().getAuthentication();
	                }
	            })
	            .withSockJS();
	}
	//수신과 발신 채널을 설정
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//[1] 사용자가 메세지를 보낼 수 있는 채널을 설정 (/app/** 로 보내주세요!)
		registry.setApplicationDestinationPrefixes("/app");
		
		//[2] 사용자가 메세지 수신을 위해 구독할 수 있는 대표 채널을 설정
		//- /public/** - 공개된 메세지가 오고가는 채널
		//- /private/** - 비공개 메세지가 오고가는 채널
		registry.enableSimpleBroker("/public", "/private");
	}
	
	//클라이언트에서 서버로 들어오는 STOMP 메세지의 채널 설정
	//→ SecurityContextChannelInterceptor를 설정해서 웹소켓 메세지의 사용자 정보를 복원
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new SecurityContextChannelInterceptor());
	}
	
	//@AuthenticationPrincipal과 같은 애노테이션을 이용한 자동 해석이 가능하도록 도구 설정
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
	}
}
