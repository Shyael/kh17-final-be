package com.kh.khedu.configuration;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfiguration {
	
	@Autowired
	private JwtProperties jwtProperties;
	
	@Bean
	public SecretKey secretKey() {
		return new SecretKeySpec(
				jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), 
				"HmacSHA256"
		);
	}
	
	@Bean
	public JwtEncoder jwtEncoder(SecretKey key) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(key));
	}
	
	@Bean
	public JwtDecoder jwtDecoder(SecretKey key) {
		return NimbusJwtDecoder.withSecretKey(key)
				.macAlgorithm(MacAlgorithm.HS256).build();
	}
	
	@Bean
	public JwsHeader jwsHeader() {
		return JwsHeader.with(MacAlgorithm.HS256).build();
	}
}
