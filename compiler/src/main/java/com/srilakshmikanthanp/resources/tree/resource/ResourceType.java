package com.srilakshmikanthanp.resources.tree.resource;

import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;

public enum ResourceType {
  STRING,
  BYTES,
  STREAM;

  public static ResourceType infer(ResourceBodyNode body) {
    return  switch (body) {
      case InlineResourceBodyNode ignored -> ResourceType.STRING;
      case FileResourceBodyNode ignored -> ResourceType.STREAM;
    };
  }
}
