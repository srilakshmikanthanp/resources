package com.srilakshmikanthanp.resources.parser.json;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.yml.YmlResourceParserV1;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

import java.io.InputStream;

/**
 * ResourceParser implementation for JSON resource files.
 *
 * <p>Example JSON format:
 * <pre>
 * {
 *   "name": "SampleJson",
 *   "resources": {
 *     "echo": "echo \"Hello, World\"",
 *     "greet": "Hello, {name}! Welcome to {place}",
 *     "script": {
 *       "file": "script.sh"
 *     }
 *   }
 * }
 * </pre>
 */
public class JsonResourceParserV1 implements ResourceParser {
  private final YmlResourceParserV1 delegate = new YmlResourceParserV1();

  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    return delegate.parse(context, stream);
  }
}
