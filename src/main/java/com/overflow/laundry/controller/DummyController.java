package com.overflow.laundry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

  @GetMapping("/")
  public String dummyEndpoint() {
    return "This is a dummy endpoint";
  }
}
