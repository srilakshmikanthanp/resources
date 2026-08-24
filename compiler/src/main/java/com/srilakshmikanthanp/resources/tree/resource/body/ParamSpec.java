package com.srilakshmikanthanp.resources.tree.resource.body;

/**
 * Represents a parameter placeholder in a template resource, with its parameter name
 * and optional Java type (e.g. "String", "int", "double", "boolean", "long").
 */
public record ParamSpec(String name, String type) {
  public ParamSpec(String name) {
    this(name, "String");
  }

  /**
   * Returns the normalized java type name.
   */
  public String normalizedType() {
    return switch (type.toLowerCase()) {
      case "int", "integer" -> "int";
      case "long" -> "long";
      case "double" -> "double";
      case "float" -> "float";
      case "boolean", "bool" -> "boolean";
      case "short" -> "short";
      case "byte" -> "byte";
      default -> type;
    };
  }

  /**
   * Returns the appropriate format specifier for String.format.
   */
  public String formatSpecifier(int index) {
    return switch (normalizedType()) {
      case "int", "long", "short", "byte" -> "%" + index + "$d";
      case "boolean" -> "%" + index + "$b";
      default -> "%" + index + "$s";
    };
  }
}
