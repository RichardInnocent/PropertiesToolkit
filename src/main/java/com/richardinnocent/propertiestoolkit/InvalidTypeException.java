package com.richardinnocent.propertiestoolkit;

public class InvalidTypeException extends PropertiesException {

  private InvalidTypeException(String message, Exception e) {
    super(message, e);
  }

  public static InvalidTypeException forProperty(String key, String value, Exception cause) {
    return new InvalidTypeException(
        String.format("Key, %s, contains a value, %s, that cannot be "
                          + "converted to the expected type", key, value), cause);
  }

}
