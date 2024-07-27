package com.sparta.goodbite.domain.waiting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/index.html"; // index.html로 리다이렉트
    }
}