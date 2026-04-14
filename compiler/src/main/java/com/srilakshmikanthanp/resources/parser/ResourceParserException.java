package com.srilakshmikanthanp.resources.parser;

public class ResourceParserException extends RuntimeException {
  public ResourceParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceParserException(String message) {
    super(message);
  }
}
