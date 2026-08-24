package com.srilakshmikanthanp.resources.parser.properties;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.body.TemplateStringResourceBodyNode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class PropertiesResourceParserV1Test {
  private final PropertiesResourceParserV1 parser = new PropertiesResourceParserV1();

  @Test
  public void shouldParsePropertiesFileCorrectly() {
    String propertiesContent = """
      name=SampleProperties
      echo=echo "Hello, World"
      greet=Hello, {name}! Count: {count:int}
      """;

    InputStream stream = new ByteArrayInputStream(propertiesContent.getBytes(StandardCharsets.UTF_8));
    Context context = new Context(new PackageResourceElement("com.example"), (pkg, path) -> InputStream.nullInputStream());

    ResourceBundleNode bundle = parser.parse(context, stream);

    assertEquals("SampleProperties", bundle.name());
    assertEquals(2, bundle.resources().size());

    TemplateStringResourceBodyNode greetBody = (TemplateStringResourceBodyNode) bundle.resources().stream()
      .filter(r -> r.name().equals("greet"))
      .findFirst()
      .orElseThrow()
      .body();

    assertEquals(2, greetBody.params().size());
    assertEquals("name", greetBody.params().get(0).name());
    assertEquals("count", greetBody.params().get(1).name());
    assertEquals("int", greetBody.params().get(1).normalizedType());
  }
}
