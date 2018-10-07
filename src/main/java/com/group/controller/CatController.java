package com.group.controller;

import com.group.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CatController {

    @Autowired
    private CatService catService;

    @RequestMapping("/cat")
    public String Cat(){
        return "kid";
    }

}
