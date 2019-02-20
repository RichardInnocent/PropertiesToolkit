package com.richardinnocent.propertiestoolkit;

public class MissingPropertyException extends PropertiesException {

  private MissingPropertyException(String message) {
    super(message);
  }

  public static MissingPropertyException forProperty(String key) {
    return new MissingPropertyException(String.format("Property, %s, is missing", key));
  }

}
