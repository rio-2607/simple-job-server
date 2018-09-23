package com.beautyboss.slogen.simplejobserver.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@Controller
@RequestMapping("/amjob/jobs")
public class MainController {

    @RequestMapping("/admin")
    public String admin() {
        return "admin";

    }
}
