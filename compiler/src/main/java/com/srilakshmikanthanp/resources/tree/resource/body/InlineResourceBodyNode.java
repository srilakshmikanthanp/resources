package com.srilakshmikanthanp.resources.tree.resource.body;

public record InlineResourceBodyNode(String content, ResourceFieldType type) implements ResourceBodyNode {
  public InlineResourceBodyNode(String content) {
    this(content, ResourceFieldType.STRING);
  }
}
