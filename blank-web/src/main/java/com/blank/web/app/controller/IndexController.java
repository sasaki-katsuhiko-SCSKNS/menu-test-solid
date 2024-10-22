package com.blank.web.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author YMSLX
 * @version 1.0
 *
 */
@Controller
public class IndexController { 

  @GetMapping("/")
  public String index() {

    return "index";
  }
 
}
