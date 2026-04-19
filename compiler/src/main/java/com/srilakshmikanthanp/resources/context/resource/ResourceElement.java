package com.srilakshmikanthanp.resources.context.resource;

public sealed interface ResourceElement permits PackageResourceElement, TypeResourceElement {
  String packageName();
}
