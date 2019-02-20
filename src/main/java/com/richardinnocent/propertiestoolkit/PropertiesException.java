package com.richardinnocent.propertiestoolkit;

public class PropertiesException extends RuntimeException {

  public PropertiesException() {
    super();
  }

  public PropertiesException(String message) {
    super(message);
  }

  public PropertiesException(String message, Exception e) {
    super(message, e);
  }

  public PropertiesException(Exception e) {
    super(e);
  }

}
