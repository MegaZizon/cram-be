package com.cram.backend.user.controller;

import com.cram.backend.user.dto.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {

        return "main route";
    }

    @GetMapping("/me")
    @ResponseBody
    public String test(@AuthenticationPrincipal CustomOAuth2User user){
        return "id=" + user.getUserId() + ", username=" + user.getUsername();
    }
}
