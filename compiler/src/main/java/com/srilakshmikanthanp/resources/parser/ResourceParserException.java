package com.srilakshmikanthanp.resources.parser;

import lombok.Getter;

@Getter
public class ResourceParserException extends RuntimeException {
  private final int line, column;

  public ResourceParserException(int line, int column, String message, Throwable cause) {
    super(message, cause);
    this.line = line;
    this.column = column;
  }

  public ResourceParserException(int line, int column, String message) {
    super(message);
    this.line = line;
    this.column = column;
  }
}
