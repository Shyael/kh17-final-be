package com.kh.khedu.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignatureEncryptor {

	//서명은 나중에 보여줘야 할 수도 있어서 양방향 암호화를 가져왔습니다.
	
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";

	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH = 128;

	private final SecretKeySpec secretKey;

	public SignatureEncryptor(
	        @Value("${security.signature-key:khedu-signature-key-2026-1234567}") String key) {

	    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

	    if (keyBytes.length != 16
	            && keyBytes.length != 24
	            && keyBytes.length != 32) {
	        throw new IllegalArgumentException(
	                "security.signature-key는 16, 24, 32바이트여야 합니다."
	        );
	    }

	    this.secretKey = new SecretKeySpec(
	            keyBytes,
	            ALGORITHM
	    );
	}
	

	//암호화
	public String encrypt(String plainText) {

		try {
			//[1] 매번 새로운 IV 생성
			byte[] iv = new byte[IV_LENGTH];

			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);


			//[2] AES-GCM 설정
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);

			GCMParameterSpec spec =
					new GCMParameterSpec(TAG_LENGTH, iv);

			cipher.init(
					Cipher.ENCRYPT_MODE,
					secretKey,
					spec
			);


			//[3] 평문 암호화
			byte[] encrypted =
					cipher.doFinal(
						plainText.getBytes(StandardCharsets.UTF_8)
					);


			//[4] IV + 암호문 합치기
			byte[] result =
					new byte[iv.length + encrypted.length];

			System.arraycopy(
					iv, 0,
					result, 0,
					iv.length
			);

			System.arraycopy(
					encrypted, 0,
					result, iv.length,
					encrypted.length
			);


			//[5] DB에 저장하기 편하도록 Base64 문자열로 변환
			return Base64.getEncoder()
					.encodeToString(result);

		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	//복호화
	public String decrypt(String encryptedText) {

		try {
			//[1] Base64 문자열을 byte[]로 복구
			byte[] result =
					Base64.getDecoder()
						.decode(encryptedText);


			//[2] 앞 12바이트는 IV
			byte[] iv = new byte[IV_LENGTH];

			System.arraycopy(
					result, 0,
					iv, 0,
					IV_LENGTH
			);


			//[3] 나머지는 실제 암호문
			byte[] encrypted =
					new byte[result.length - IV_LENGTH];

			System.arraycopy(
					result, IV_LENGTH,
					encrypted, 0,
					encrypted.length
			);


			//[4] 복호화 설정
			Cipher cipher =
					Cipher.getInstance(TRANSFORMATION);

			GCMParameterSpec spec =
					new GCMParameterSpec(TAG_LENGTH, iv);

			cipher.init(
					Cipher.DECRYPT_MODE,
					secretKey,
					spec
			);


			//[5] 원문 복원
			byte[] decrypted =
					cipher.doFinal(encrypted);

			return new String(
					decrypted,
					StandardCharsets.UTF_8
			);

		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}