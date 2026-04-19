package com.srilakshmikanthanp.resources.parser.yml;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceFieldType;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YmlResourceParserV1 implements ResourceParser {
  private static final String NAME_KEY = "name";
  private static final String RESOURCES_KEY = "resources";
  private static final String INLINE_KEY = "inline";
  private static final String FILE_KEY = "file";
  private static final String TYPE_KEY = "type";

  private ResourceNode parseResource(String name, Object node) {
    return new ResourceNode(name, parseBody(node));
  }

  @SuppressWarnings("unchecked")
  private ResourceBodyNode parseBody(Object node) {
    if (node instanceof String) {
      return new InlineResourceBodyNode((String) node, ResourceFieldType.STRING);
    }
    if (!(node instanceof Map)) {
      throw new ResourceParserException("Invalid node type: " + node.getClass());
    }
    var map = (Map<String, String>) node;
    if (map.containsKey(INLINE_KEY)) {
      ResourceFieldType type = ResourceFieldType.valueOf(map.getOrDefault(TYPE_KEY, ResourceFieldType.STRING.toString()));
      String inline = map.get(INLINE_KEY);
      return new InlineResourceBodyNode(inline, type);
    } else if (map.containsKey(FILE_KEY)) {
      ResourceFieldType type = ResourceFieldType.valueOf(map.getOrDefault(TYPE_KEY, ResourceFieldType.STREAM.toString()));
      String file = map.get(FILE_KEY);
      return new FileResourceBodyNode(file, type);
    } else {
      throw new ResourceParserException(String.format("Resource definition must contain either '%s' or '%s' key", INLINE_KEY, FILE_KEY));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    Map<String, Object> root = new Yaml().load(stream);

    if (root == null) {
      throw new ResourceParserException("Empty YAML document");
    }

    Object bundleName = root.get(NAME_KEY);

    if (!(bundleName instanceof String)) {
      throw new ResourceParserException(String.format("Missing required '%s' field", NAME_KEY));
    }

    Object resources = root.get(RESOURCES_KEY);

    if (resources == null) {
      throw new ResourceParserException(String.format("Missing required '%s' field", RESOURCES_KEY));
    }

    if (!(resources instanceof Map)) {
      throw new ResourceParserException(String.format("'%s' field must be a map of resource names to resource definitions", RESOURCES_KEY));
    }

    List<ResourceNode> resourceNodes = new LinkedList<>();

    for (var entry : ((Map<String, Object>) resources).entrySet()) {
      resourceNodes.add(parseResource(entry.getKey(), entry.getValue()));
    }

    return new ResourceBundleNode((String) bundleName, resourceNodes);
  }
}
