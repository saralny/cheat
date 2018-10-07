package com.group.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game")
public class GameController {

    @RequestMapping("/kid")
    public String Cat(){
        return "game/kid";
    }

}
