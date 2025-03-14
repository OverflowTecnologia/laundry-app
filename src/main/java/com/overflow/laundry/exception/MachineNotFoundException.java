package com.overflow.laundry.exception;

public class MachineNotFoundException extends RuntimeException {
  public MachineNotFoundException(String message) {
    super(message);
  }
}
