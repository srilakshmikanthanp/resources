package com.srilakshmikanthanp.resources.parser.xml;


import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.context.resource.ResourceElement;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class XmlResourceParserV1Test {
  private final ResourceParser parser = new XmlResourceParserV1();

  @Test
  public void shouldParseXml() throws Exception {
    try (InputStream stream = this.getClass().getResourceAsStream("sample.xml")) {
      ResourceElement resourceElement = new PackageResourceElement("com.srilakshmikanthanp.resources");
      Context context = new Context(resourceElement, (packageName, path) -> InputStream.nullInputStream());
      ResourceBundleNode node = parser.parse(context, stream);

      assertEquals("com.srilakshmikanthanp.resources", resourceElement.packageName());
      assertEquals("TestXml", node.name());
      assertEquals(3, node.resources().size());

      assertEquals("echo", node.resources().get(0).name());
      assertInstanceOf(InlineResourceBodyNode.class, node.resources().get(0).body());

      assertEquals("print", node.resources().get(1).name());
      assertInstanceOf(InlineResourceBodyNode.class, node.resources().get(1).body());

      assertEquals("config", node.resources().get(2).name());
      assertInstanceOf(FileResourceBodyNode.class, node.resources().get(2).body());
    }
  }
}
