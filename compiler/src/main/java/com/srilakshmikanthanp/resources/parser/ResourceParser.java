package com.srilakshmikanthanp.resources.parser;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

import java.io.InputStream;

public interface ResourceParser {
  ResourceBundleNode parse(Context context, InputStream stream);
}
