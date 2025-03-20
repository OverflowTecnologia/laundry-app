package com.overflow.laundry.exception;

public class MachineIdentifierAlreadyInUseException extends RuntimeException {
  public MachineIdentifierAlreadyInUseException(String message) {
    super(message);
  }
}
