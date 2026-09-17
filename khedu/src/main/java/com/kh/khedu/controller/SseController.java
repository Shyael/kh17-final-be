package com.kh.khedu.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.sse.SseClient;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.sse.SseAlarmVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sse")
public class SseController {
	// 접속한 사용자들의 Emitter를 관리하는 Thread-safe한 Map (Key: accountNo, Value: SseEmitter)
    private static final Map<String, Map<Integer, SseClient>> typeClients = new ConcurrentHashMap<>();

    // 1. 클라이언트가 실시간 알람 수신을 위해 최초 연결을 맺는 엔드포인트
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@CurrentUser TokenParseResponseVO parseVO) {
        // 타임아웃 설정 (30분 = 30 * 60 * 1000ms). 시간이 지나면 연결이 끊기고 클라이언트가 재연결을 시도합니다.
    	Integer accountNo = parseVO.getAccountNo();
    	String accountType = parseVO.getAccountType();
    	List<String> roleNames = parseVO.getRoleNames();
    	
    	log.info("parseVO = {}", parseVO);
    	
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        
        SseClient client = new SseClient(emitter, roleNames != null ? roleNames : Collections.emptyList());
        
        // 해당 타입의 맵이 없으면 새로 생성하고 집어넣습니다.
        typeClients.computeIfAbsent(accountType, k -> new ConcurrentHashMap<>()).put(accountNo, client);

        // 연결 종료, 타임아웃, 에러 발생 시 메모리에서 안전하게 제거
        Runnable removeClient = () -> {
            Map<Integer, SseClient> map = typeClients.get(accountType);
            if (map != null) map.remove(accountNo);
        };

        // 연결 종료, 타임아웃, 에러 발생 시 메모리(Map)에서 제거
        emitter.onCompletion(removeClient);
        emitter.onTimeout(removeClient);
        emitter.onError((e) -> removeClient.run());

        // 연결 직후 데이터 유실(503 에러 등) 방지용 더미 이벤트 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected successfully as " + accountType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        log.warn("parseVO = {}", parseVO);
        
        return emitter;
    }

    public static void sendToGroup(String accountType, String roleName, String message) {
        Map<Integer, SseClient> map = typeClients.get(accountType);
        log.info("현재 typeClients 맵 상태: {}", typeClients.keySet());
        log.info("찾으려는 accountType: [{}]", accountType);
        
        if (map == null) return;

        map.forEach((accountNo, client) -> {
            boolean shouldSend = false;

            log.warn("roleNames = {}", client.roleNames());
            
            // 1. roleName이 null이거나 비어있다면 -> 해당 타입 전체에게 전송
            if (roleName == null || roleName.trim().isEmpty()) {
                shouldSend = true;
            } 
            // 2. 특정 roleName이 지정되어 있다면 -> 유저의 roleNames 목록에 포함되어 있는지 체크
            else if (client.roleNames() != null && client.roleNames().contains(roleName)) {
                shouldSend = true;
            }

            // 조건에 부합하면 SSE 이벤트 전송
            if (shouldSend) {
                try {
                	log.info("alarm 전송");
                    client.getEmitter().send(SseEmitter.event().name("alarm").data(message));
                } catch (IOException e) {
                    map.remove(accountNo); // 에러 발생 시 끊긴 연결로 판단하여 제거
                }
            }
        });
    }
    
    // 💡 2. [추가] 특정 유저(단일 대상)에게만 알람을 보내는 메서드
    public static void sendToUser(String accountType, Integer accountNo, String message) {
        Map<Integer, SseClient> map = typeClients.get(accountType);
        if (map == null) return;

        SseClient client = map.get(accountNo);
        if (client != null) {
            try {
                client.getEmitter().send(SseEmitter.event().name("alarm").data(message));
            } catch (IOException e) {
                map.remove(accountNo); // 에러 발생 시 끊긴 연결로 판단하여 제거
            }
        }
    }
    

    // 3.[추가] 특정 유저(단일 대상) 클릭하면 상세로 갈 수 있는 알람을 보내는 메서드
    public static void sendToUser(String accountType, Integer accountNo, SseAlarmVO alarm) {
        Map<Integer, SseClient> map = typeClients.get(accountType);
        if (map == null) return;

        SseClient client = map.get(accountNo);
        if (client != null) {
            try {
                client.getEmitter().send(SseEmitter.event().name("alarm").data(alarm));
            }
            catch (IOException e) {
                map.remove(accountNo);// 에러 발생 시 끊긴 연결로 판단하여 제거
            }
        }

    }
}
