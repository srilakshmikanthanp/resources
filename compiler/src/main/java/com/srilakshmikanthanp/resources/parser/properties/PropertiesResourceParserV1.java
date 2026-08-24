package com.srilakshmikanthanp.resources.parser.properties;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.TemplateStringResourceBodyNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * ResourceParser implementation for Java .properties files.
 *
 * <p>Example properties format:
 * <pre>
 * name=SampleProperties
 * echo=echo "Hello World"
 * greet=Hello, {name}!
 * script.file=script.sh
 * </pre>
 */
public class PropertiesResourceParserV1 implements ResourceParser {
  private static final String BUNDLE_NAME_KEY = "name";
  private static final String FILE_SUFFIX = ".file";
  private static final String TYPE_SUFFIX = ".type";

  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    Properties props = new Properties();
    try {
      props.load(stream);
    } catch (IOException e) {
      throw new ResourceParserException(0, 0, "Failed to load properties file: " + e.getMessage());
    }

    String bundleName = props.getProperty(BUNDLE_NAME_KEY, "PropertiesResource");
    List<ResourceNode> resources = new ArrayList<>();

    for (String key : props.stringPropertyNames()) {
      if (key.equals(BUNDLE_NAME_KEY) || key.endsWith(TYPE_SUFFIX)) {
        continue;
      }

      String value = props.getProperty(key);
      ResourceBodyNode body;
      ResourceType type;

      if (key.endsWith(FILE_SUFFIX)) {
        String resName = key.substring(0, key.length() - FILE_SUFFIX.length());
        body = new FileResourceBodyNode(value);
        String explicitType = props.getProperty(resName + TYPE_SUFFIX);
        type = explicitType != null ? ResourceType.valueOf(explicitType) : ResourceType.infer(body);
        resources.add(new ResourceNode(resName, body, type));
      } else {
        body = TemplateStringResourceBodyNode.parse(value);
        String explicitType = props.getProperty(key + TYPE_SUFFIX);
        type = explicitType != null ? ResourceType.valueOf(explicitType) : ResourceType.infer(body);

        if (body instanceof TemplateStringResourceBodyNode && type != ResourceType.STRING) {
          throw new ResourceParserException(0, 0, String.format(
            "Resource '%s' has {placeholder} parameters, so it must have type STRING (found %s)",
            key, type
          ));
        }
        resources.add(new ResourceNode(key, body, type));
      }
    }

    return new ResourceBundleNode(bundleName, resources);
  }
}
