package com.srilakshmikanthanp.resources.parser;

import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

import java.io.InputStream;

public interface ResourceParser {
  /**
   * Parses the given path and returns a ResourceBundle.
   *
   * @param stream the input stream of the resource to be parsed
   * @return the parsed ResourceBundle
   */
  ResourceBundleNode parse(InputStream stream);
}
