package com.kh.khedu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(
	expression = "#this instanceof T(org.springframework.security.oauth2.jwt.Jwt) ? @jwtService.parseAccessToken(#this.tokenValue) : null",
	errorOnInvalidType = true 
)
public @interface CurrentUser {
//	boolean required() default true;//required=true 옵션을 내장시킨다 
}
