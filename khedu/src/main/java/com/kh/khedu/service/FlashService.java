package com.kh.khedu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;

//임시 데이터를 저장하기 위한 서비스
@Service
public class FlashService {
//	웹소켓 Version 3 용도의 플래시 저장소
//	- 사용자의 정보 (TokenParseResponseVO)
//	- 동기화 된 Map을 사용
//	- accountId를 key로 사용
	private Map<String, TokenParseResponseVO> userVersion3
										= new ConcurrentHashMap<>();
	public void enter(TokenParseResponseVO parseVO) {
		userVersion3.put(parseVO.getAccountId(), parseVO);
	}
	public void leave(TokenParseResponseVO parseVO) {
		userVersion3.remove(parseVO.getAccountId());
	}
	public List<TokenParseResponseVO> list() {
		return new ArrayList<>(userVersion3.values());
	}
}
