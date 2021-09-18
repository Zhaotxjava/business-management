package com.hfi.insurance.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackController {

    @RequestMapping("check_health")
    public String checkHealth() {
        return "ok";
    }

}
