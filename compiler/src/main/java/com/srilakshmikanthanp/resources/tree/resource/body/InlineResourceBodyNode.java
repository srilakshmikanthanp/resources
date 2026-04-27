package com.srilakshmikanthanp.resources.tree.resource.body;

public sealed interface InlineResourceBodyNode extends ResourceBodyNode permits InlineBytesResourceBodyNode, InlineStringResourceBodyNode {
  static InlineResourceBodyNode of(String content) {
    return new InlineStringResourceBodyNode(content);
  }

  static InlineResourceBodyNode of(byte... bytes) {
    return new InlineBytesResourceBodyNode(bytes);
  }
}
