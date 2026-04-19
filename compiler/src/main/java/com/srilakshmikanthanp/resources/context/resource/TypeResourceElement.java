package com.srilakshmikanthanp.resources.context.resource;

public sealed interface TypeResourceElement extends ResourceElement permits ClassResourceElement, InterfaceResourceElement {
  String name();
}
