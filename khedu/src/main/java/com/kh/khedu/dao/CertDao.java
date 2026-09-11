package com.kh.khedu.dao;

import com.kh.khedu.dto.CertDto;

public interface CertDao {
	void add(CertDto certDto);
	boolean change(CertDto certDto);
	CertDto find(String certEmail);
	boolean delete(String certEmail);
	boolean use(String certEmail);
}
