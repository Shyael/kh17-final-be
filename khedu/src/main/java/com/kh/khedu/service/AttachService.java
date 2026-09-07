package com.kh.khedu.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.vo.attach.AttachInfoVO;

public interface AttachService {
	int save(MultipartFile attach) throws IllegalStateException, IOException;
	void delete(Integer attachNo);
	
	AttachInfoVO load(int attachNo) throws IOException;
}
