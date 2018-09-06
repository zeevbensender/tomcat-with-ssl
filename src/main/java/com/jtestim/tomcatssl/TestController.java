package com.jtestim.tomcatssl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {
	@GetMapping("/test")
	public String test(HttpServletRequest request) {
		int serverPort = request.getLocalPort();
		return "Server port is " + serverPort;
	}
}
