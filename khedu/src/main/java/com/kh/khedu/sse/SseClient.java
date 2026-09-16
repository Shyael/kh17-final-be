package com.kh.khedu.sse;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseClient {
	private SseEmitter emitter;
    private List<String> roleNames;

    public SseClient(SseEmitter emitter, List<String> roleNames) {
        this.emitter = emitter;
        this.roleNames = roleNames;
    }

    public SseEmitter getEmitter() { return emitter; }
    public List<String> roleNames() { return roleNames; }
}
