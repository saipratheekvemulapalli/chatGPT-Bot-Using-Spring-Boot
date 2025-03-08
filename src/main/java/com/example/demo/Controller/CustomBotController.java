package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.BotService;

@RestController
@RequestMapping("/bot")
public class CustomBotController {

    @Autowired
    private BotService botService;

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        return botService.callOpenAIWithRetry(prompt, 3);
    }
}
