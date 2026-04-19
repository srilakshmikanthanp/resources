package com.srilakshmikanthanp.resources.compiler.java.v1;

import com.palantir.javapoet.*;
import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.context.resource.ClassResourceElement;
import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.InterfaceResourceElement;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineResource;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceFieldType;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class JavaResourceCompilerV1 implements ResourceCompiler {
  private MethodSpec fileResource(String resourceName, FileResourceBodyNode resourceNode) {
    return MethodSpec.methodBuilder(resourceName)
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resourceNode.type()))
      .addStatement("return $L",  CodeBlock.of("new $T(getClass().getResource($S).getPath()).$L", FileResource.class, resourceNode.path(), getConversion(resourceNode.type())))
      .build();
  }

	private MethodSpec inlineResource(String resourceName, InlineResourceBodyNode resourceNode) {
		return MethodSpec.methodBuilder(resourceName)
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resourceNode.type()))
      .addStatement("return $L", CodeBlock.of("new $T($S).$L", InlineResource.class, resourceNode.content(), getConversion(resourceNode.type())))
      .build();
	}

  private Class<?> getResourceClass(ResourceFieldType type) {
    return switch (type) {
      case STRING -> String.class;
      case BYTES -> byte[].class;
      case STREAM -> java.io.InputStream.class;
    };
  }

  private String getConversion(ResourceFieldType type) {
    return switch (type) {
      case STRING -> "asString()";
      case BYTES -> "asBytes()";
      case STREAM -> "asStream()";
    };
  }

  private MethodSpec constructor() {
    return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
  }

  private FieldSpec instanceField(String packageName, String className) {
    return FieldSpec.builder(ClassName.get(packageName, className), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("new $T()", ClassName.get(packageName, className))
      .build();
  }

	@Override
	public List<CompiledResource> compile(Context context, ResourceBundleNode bundle) {
		TypeSpec.Builder resourceBuilder = TypeSpec.classBuilder(bundle.name()).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    resourceBuilder.addField(instanceField(context.resourceElement().packageName(), bundle.name()));
    resourceBuilder.addMethod(constructor());

    switch (context.resourceElement()) {
      case InterfaceResourceElement element -> resourceBuilder.addSuperinterface(ClassName.get(element.packageName(), element.name()));
      case ClassResourceElement element -> resourceBuilder.superclass(ClassName.get(element.packageName(), element.name()));
      case PackageResourceElement ignored -> {}
    }

		for (ResourceNode resource : bundle.resources()) {
      switch (resource.body()) {
        case InlineResourceBodyNode body -> resourceBuilder.addMethod(inlineResource(resource.name(), body));
        case FileResourceBodyNode body -> resourceBuilder.addMethod(fileResource(resource.name(), body));
      }
		}

		TypeSpec resource = resourceBuilder.build();

		try {
			JavaFile javaFile = JavaFile.builder(context.resourceElement().packageName(), resource).build();
			ArrayList<CompiledResource> compiledResources = new ArrayList<>();
			StringBuilder output = new StringBuilder();
			javaFile.writeTo(output);
			compiledResources.add(new CompiledResource(context.resourceElement().packageName(), resource.name(), output.toString()));
			return compiledResources;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
