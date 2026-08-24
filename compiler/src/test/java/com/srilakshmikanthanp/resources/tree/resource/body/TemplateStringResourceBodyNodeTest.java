package com.srilakshmikanthanp.resources.tree.resource.body;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TemplateStringResourceBodyNodeTest {
  @Test
  public void shouldReturnPlainInlineNodeWhenNoPlaceholders() {
    InlineResourceBodyNode body = TemplateStringResourceBodyNode.parse("echo \"Hello, World\"");
    assertInstanceOf(InlineStringResourceBodyNode.class, body);
    assertEquals("echo \"Hello, World\"", ((InlineStringResourceBodyNode) body).content());
  }

  @Test
  public void shouldExtractSinglePlaceholder() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode) TemplateStringResourceBodyNode.parse("Hello, {name}!");
    assertEquals("Hello, %1$s!", body.format());
    assertEquals(1, body.params().size());
    assertEquals("name", body.params().get(0).name());
    assertEquals("String", body.params().get(0).type());
  }

  @Test
  public void shouldExtractMultiplePlaceholdersInOrder() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("Hello, {name}! Welcome to {place}");
    assertEquals("Hello, %1$s! Welcome to %2$s", body.format());
    assertEquals(2, body.params().size());
    assertEquals("name", body.params().get(0).name());
    assertEquals("place", body.params().get(1).name());
  }

  @Test
  public void shouldExtractTypedPlaceholders() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("Order #{id:long} count: {count:int} price: {price:double} active: {active:boolean}");
    assertEquals("Order #%1$d count: %2$d price: %3$s active: %4$b", body.format());
    assertEquals(4, body.params().size());

    assertEquals("id", body.params().get(0).name());
    assertEquals("long", body.params().get(0).normalizedType());

    assertEquals("count", body.params().get(1).name());
    assertEquals("int", body.params().get(1).normalizedType());

    assertEquals("price", body.params().get(2).name());
    assertEquals("double", body.params().get(2).normalizedType());

    assertEquals("active", body.params().get(3).name());
    assertEquals("boolean", body.params().get(3).normalizedType());
  }

  @Test
  public void shouldReuseParamWhenPlaceholderRepeats() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("{name} said hi to {name}");
    assertEquals("%1$s said hi to %1$s", body.format());
    assertEquals(1, body.params().size());
    assertEquals("name", body.params().get(0).name());
  }

  @Test
  public void shouldSupportEscapedLiteralBraces() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("{{literal}} {value}");
    assertEquals("{literal} %1$s", body.format());
    assertEquals(1, body.params().size());
    assertEquals("value", body.params().get(0).name());
  }

  @Test
  public void shouldEscapePercentSignsInSurroundingText() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("Discount: 10% off {item}");
    assertEquals("Discount: 10%% off %1$s", body.format());
    assertEquals("item", body.params().get(0).name());
  }
}
