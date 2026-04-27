package com.srilakshmikanthanp.resources.preprocessor.inliner;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.preprocessor.ResourcePreprocessor;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

public class FileStringInliner implements ResourcePreprocessor {
  private String readResourceContent(Context context, String path) {
    try (InputStream stream = context.resourceReader().read(context.resourceElement().packageName(), path)) {
      return new String(stream.readAllBytes());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private ResourceNode transform(Context context, ResourceNode node) {
    if (node.type() == ResourceType.STRING && node.body() instanceof FileResourceBodyNode(String path)) {
      return new ResourceNode(node.name(), InlineResourceBodyNode.of(readResourceContent(context, path)), ResourceType.STRING);
    } else {
      return node;
    }
  }

  @Override
  public ResourceBundleNode process(Context context, ResourceBundleNode bundle) {
    List<ResourceNode> resources = bundle.resources().stream().map(node -> transform(context, node)).toList();
    return new ResourceBundleNode(bundle.name(), resources);
  }
}
