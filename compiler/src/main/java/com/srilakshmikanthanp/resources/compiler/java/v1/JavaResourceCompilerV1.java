package com.srilakshmikanthanp.resources.compiler.java.v1;

import com.palantir.javapoet.*;
import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
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

  private FieldSpec instanceField(JavaResourceCompilerContext ctx) {
    return FieldSpec.builder(ClassName.get(ctx.packageName(), ctx.className()), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("new $T()", ClassName.get(ctx.packageName(), ctx.className()))
      .build();
  }

	@Override
	public List<CompiledResource> compile(ResourceBundleNode bundle) {
		JavaResourceCompilerContext ctx = new JavaResourceCompilerContext(bundle.getPackageName(), bundle.getName());
		TypeSpec.Builder resourceBuilder = TypeSpec.classBuilder(bundle.getName()).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    resourceBuilder.addField(instanceField(ctx));
    resourceBuilder.addMethod(constructor());

    if (bundle.getImplementing().isPresent()) {
      resourceBuilder.addSuperinterface(ClassName.get(bundle.getPackageName(), bundle.getImplementing().get()));
    }

		for (ResourceNode resource : bundle.getResources()) {
			if (resource.getBody() instanceof InlineResourceBodyNode) {
        resourceBuilder.addMethod(inlineResource(resource.getName(), (InlineResourceBodyNode) resource.getBody()));
			} else if (resource.getBody() instanceof FileResourceBodyNode) {
        resourceBuilder.addMethod(fileResource(resource.getName(), (FileResourceBodyNode) resource.getBody()));
			} else {
				throw new IllegalArgumentException("Unsupported resource body type: " + resource.getBody().getClass().getName());
			}
		}

		TypeSpec resource = resourceBuilder.build();

		try {
			JavaFile javaFile = JavaFile.builder(bundle.getPackageName(), resource).build();
			ArrayList<CompiledResource> compiledResources = new ArrayList<>();
			StringBuilder output = new StringBuilder();
			javaFile.writeTo(output);
			compiledResources.add(new CompiledResource(bundle.getPackageName(), resource.name(), output.toString()));
			return compiledResources;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
