package com.overflow.laundry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessController {

  @GetMapping("/home")
  public String homeEndpoint() {
    return "This is a home endpoint";
  }

  @GetMapping("/farewell")
  public String farewellEndpoint() {
    return "See you next time!";
  }
}
