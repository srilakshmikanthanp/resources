package com.srilakshmikanthanp.resources.parser.json;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.body.TemplateStringResourceBodyNode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonResourceParserV1Test {
  private final JsonResourceParserV1 parser = new JsonResourceParserV1();

  @Test
  public void shouldParseJsonResourceFileCorrectly() {
    String jsonContent = """
      {
        "name": "SampleJson",
        "resources": {
          "echo": "echo \\"Hello, World\\"",
          "greet": "Hello, {name}! Total: ${price:double}"
        }
      }
      """;

    InputStream stream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
    Context context = new Context(new PackageResourceElement("com.example"), (pkg, path) -> InputStream.nullInputStream());

    ResourceBundleNode bundle = parser.parse(context, stream);

    assertEquals("SampleJson", bundle.name());
    assertEquals(2, bundle.resources().size());

    TemplateStringResourceBodyNode greetBody = (TemplateStringResourceBodyNode) bundle.resources().stream()
      .filter(r -> r.name().equals("greet"))
      .findFirst()
      .orElseThrow()
      .body();

    assertEquals(2, greetBody.params().size());
    assertEquals("name", greetBody.params().get(0).name());
    assertEquals("price", greetBody.params().get(1).name());
    assertEquals("double", greetBody.params().get(1).normalizedType());
  }
}
