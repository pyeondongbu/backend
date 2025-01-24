package com.pyeon.domain.post.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {
    
    @GetMapping("/")
    public String home() {
        return "API 서버가 정상적으로 실행 중입니다.";
    }
}
