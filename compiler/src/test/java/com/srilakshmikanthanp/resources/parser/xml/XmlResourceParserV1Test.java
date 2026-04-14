package com.srilakshmikanthanp.resources.parser.xml;


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
      ResourceBundleNode node = parser.parse(stream);

      assertEquals("com.srilakshmikanthanp.resources", node.getPackageName());
      assertEquals("TestXml", node.getName());
      assertEquals("com.srilakshmikanthanp.resources.TestResource", node.getImplementing().orElse(null));
      assertEquals(3, node.getResources().size());

      assertEquals("echo", node.getResources().get(0).getName());
      assertInstanceOf(InlineResourceBodyNode.class, node.getResources().get(0).getBody());

      assertEquals("print", node.getResources().get(1).getName());
      assertInstanceOf(InlineResourceBodyNode.class, node.getResources().get(1).getBody());

      assertEquals("config", node.getResources().get(2).getName());
      assertInstanceOf(FileResourceBodyNode.class, node.getResources().get(2).getBody());
    }
  }
}
