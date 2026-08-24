package com.srilakshmikanthanp.resources.compiler.java.v1;

import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.TemplateStringResourceBodyNode;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaResourceCompilerV1Test {
  private final ResourceCompiler compiler = new JavaResourceCompilerV1();

  @Test
  public void shouldGenerateMethodWithParametersForTemplateResource() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("Hello, {name}! Welcome to {place}");
    ResourceNode resourceNode = new ResourceNode("greet", body, ResourceType.STRING);
    ResourceBundleNode bundle = new ResourceBundleNode("Sample1Xml", List.of(resourceNode));
    Context context = new Context(new PackageResourceElement("com.example"), (pkg, path) -> InputStream.nullInputStream());

    List<CompiledResource> compiled = compiler.compile(context, bundle);

    assertEquals(1, compiled.size());
    String source = compiled.get(0).content();

    // Method takes two String parameters, one per distinct placeholder.
    assertTrue(source.contains("public String greet(String name, String place)"), source);
    // Body formats the positional pattern at call time rather than baking a fixed value.
    assertTrue(source.contains("String.format(\"Hello, %1$s! Welcome to %2$s\", name, place)"), source);
  }

  @Test
  public void shouldGenerateTypedMethodParametersForTypedPlaceholders() {
    TemplateStringResourceBodyNode body = (TemplateStringResourceBodyNode)
      TemplateStringResourceBodyNode.parse("Order #{id:long} item: {name:String} count: {qty:int} price: {cost:double}");
    ResourceNode resourceNode = new ResourceNode("orderInfo", body, ResourceType.STRING);
    ResourceBundleNode bundle = new ResourceBundleNode("OrderBundle", List.of(resourceNode));
    Context context = new Context(new PackageResourceElement("com.example"), (pkg, path) -> InputStream.nullInputStream());

    List<CompiledResource> compiled = compiler.compile(context, bundle);

    assertEquals(1, compiled.size());
    String source = compiled.get(0).content();

    // Method takes long, String, int, double
    assertTrue(source.contains("public String orderInfo(long id, String name, int qty, double cost)"), source);
    assertTrue(source.contains("String.format(\"Order #%1$d item: %2$s count: %3$d price: %4$s\", id, name, qty, cost)"), source);
  }
}
