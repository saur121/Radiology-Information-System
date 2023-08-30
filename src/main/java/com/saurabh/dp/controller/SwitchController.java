package com.saurabh.dp.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tst")
public class SwitchController {
      
	@GetMapping("/")
	public void handleGet(HttpServletResponse response) {
	    response.setHeader("Location", "http://localhost:3000/");
	    response.setStatus(302);
	}
}
