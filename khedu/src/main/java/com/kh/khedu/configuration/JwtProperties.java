package com.kh.khedu.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties {
	private String issuer;
	private String secret;
	private long tokenValidity;
}
