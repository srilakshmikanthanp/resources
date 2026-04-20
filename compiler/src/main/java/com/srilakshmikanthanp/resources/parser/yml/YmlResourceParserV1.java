package com.srilakshmikanthanp.resources.parser.yml;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class YmlResourceParserV1 implements ResourceParser {
  private static final String NAME_KEY = "name";
  private static final String RESOURCES_KEY = "resources";
  private static final String INLINE_KEY = "inline";
  private static final String FILE_KEY = "file";
  private static final String TYPE_KEY = "type";

  private ResourceParserException error(Mark mark, String message) {
    return new ResourceParserException(mark.getLine() + 1,  mark.getColumn() + 1, message);
  }

  private <T> T asNode(Node node, Class<T> clazz) {
    if (!clazz.isInstance(node)) {
      throw error(node.getStartMark(), String.format("Expected a %s node but got a %s node", clazz.getSimpleName(), node.getNodeId()));
    } else {
      return clazz.cast(node);
    }
  }

  private ScalarNode scalar(Node node) {
    return asNode(node, ScalarNode.class);
  }

  private List<ResourceNode> parseResources(MappingNode node) {
    return node.getValue().stream().map(entry -> parseResource(asNode(entry.getKeyNode(), ScalarNode.class).getValue(), entry.getValueNode())).toList();
  }

  private ResourceNode parseResource(String name, Node node) {
    if (node instanceof ScalarNode scalarNode) {
      ResourceBodyNode body = new InlineResourceBodyNode(scalarNode.getValue());
      return new ResourceNode(name, body, ResourceType.infer(body));
    }

    if (!(node instanceof MappingNode mappingNode)) {
      throw error(node.getStartMark(), String.format("Expected a ScalarNode or MappingNode but got a %s node", node.getNodeId()));
    }

    ResourceBodyNode body = null;
    ResourceType type = null;

    for (NodeTuple entry : mappingNode.getValue()) {
      switch (asNode(entry.getKeyNode(), ScalarNode.class).getValue()) {
        case INLINE_KEY -> body = new InlineResourceBodyNode(scalar(entry.getValueNode()).getValue());
        case FILE_KEY -> body = new FileResourceBodyNode(scalar(entry.getValueNode()).getValue());
        case TYPE_KEY -> type = ResourceType.valueOf(scalar(entry.getValueNode()).getValue());
      }
    }

    if (body == null) {
      throw error(node.getStartMark(), "Resource definition must contain either an 'inline' or 'file' field");
    }

    if (type == null) {
      type = ResourceType.infer(body);
    }

    return new ResourceNode(name, body, type);
  }

  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    MappingNode node = asNode(new Yaml().compose(new InputStreamReader(stream)), MappingNode.class);

    List<ResourceNode> resources = null;
    String bundleName = null;

    for (NodeTuple entry : node.getValue()) {
      switch (scalar(entry.getKeyNode()).getValue()) {
        case RESOURCES_KEY -> resources = parseResources(asNode(entry.getValueNode(), MappingNode.class));
        case NAME_KEY -> bundleName = scalar(entry.getValueNode()).getValue();
        default -> throw error(entry.getKeyNode().getStartMark(), String.format("Unexpected field '%s'", scalar(entry.getKeyNode()).getValue()));
      }
    }

    if (bundleName == null) {
      throw error(node.getStartMark(), String.format("Missing required '%s' field", NAME_KEY));
    }

    if (resources == null) {
      throw error(node.getStartMark(), String.format("Missing required '%s' field", RESOURCES_KEY));
    }

    return new ResourceBundleNode(bundleName, resources);
  }
}
