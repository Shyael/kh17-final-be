package com.kh.khedu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeRestController {
	@GetMapping("/active")
	public String active() {
		return "Server is running...";
	}
}
