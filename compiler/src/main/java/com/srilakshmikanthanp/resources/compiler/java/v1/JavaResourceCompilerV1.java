package com.srilakshmikanthanp.resources.compiler.java.v1;

import com.palantir.javapoet.*;
import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.InterfaceResourceElement;
import com.srilakshmikanthanp.resources.context.resource.PackageResourceElement;
import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineBytesResource;
import com.srilakshmikanthanp.resources.resource.InlineStringResource;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineBytesResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineStringResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ParamSpec;
import com.srilakshmikanthanp.resources.tree.resource.body.TemplateStringResourceBodyNode;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JavaResourceCompilerV1 implements ResourceCompiler {
  private MethodSpec.Builder inline(ResourceNode resource, InlineStringResourceBodyNode body) {
    return MethodSpec.methodBuilder(resource.name())
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resource.type()))
      .addStatement("return $L", CodeBlock.of("new $T($S).$L", InlineStringResource.class, body.content(), getConversion(resource.type())));
  }

  private MethodSpec.Builder inline(ResourceNode resource, InlineBytesResourceBodyNode body) {
    return MethodSpec.methodBuilder(resource.name())
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resource.type()))
      .addStatement("return $L", CodeBlock.of("new $T($L).$L", InlineBytesResource.class, bytes(body.content()), getConversion(resource.type())));
  }

  private MethodSpec.Builder inline(ResourceNode resource, InlineResourceBodyNode body) {
    return switch (body) {
      case InlineStringResourceBodyNode stringBody -> inline(resource, stringBody);
      case InlineBytesResourceBodyNode bytesBody -> inline(resource, bytesBody);
      case TemplateStringResourceBodyNode templateBody -> template(resource, templateBody);
    };
  }

  private MethodSpec.Builder template(ResourceNode resource, TemplateStringResourceBodyNode body) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(resource.name())
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resource.type()));

    for (ParamSpec param : body.params()) {
      builder.addParameter(getParamTypeName(param), param.name());
    }

    CodeBlock.Builder arguments = CodeBlock.builder().add("$S", body.format());
    for (ParamSpec param : body.params()) {
      arguments.add(", $L", param.name());
    }

    return builder.addStatement("return $T.format($L)", String.class, arguments.build());
  }

  private TypeName getParamTypeName(ParamSpec param) {
    return switch (param.normalizedType()) {
      case "int" -> TypeName.INT;
      case "long" -> TypeName.LONG;
      case "double" -> TypeName.DOUBLE;
      case "float" -> TypeName.FLOAT;
      case "boolean" -> TypeName.BOOLEAN;
      case "short" -> TypeName.SHORT;
      case "byte" -> TypeName.BYTE;
      default -> ClassName.get("java.lang", param.normalizedType());
    };
  }

  private MethodSpec.Builder file(ResourceNode resource, FileResourceBodyNode body) {
    CodeBlock block = CodeBlock.of(
      "new $T($T.requireNonNull(getClass().getResource($S), $S).getPath()).$L",
      FileResource.class,
      Objects.class,
      body.path(),
      "Resource not found: " + body.path(),
      getConversion(resource.type())
    );
    return MethodSpec.methodBuilder(resource.name())
      .addModifiers(Modifier.PUBLIC)
      .returns(getResourceClass(resource.type()))
      .addStatement("return $L",  block);
  }

  private CodeBlock bytes(byte[] bytes) {
    CodeBlock.Builder builder = CodeBlock.builder();
    builder.add("new byte[] {");
    List<CodeBlock> blocks = new ArrayList<>();
    for (byte b : bytes) {
      blocks.add(CodeBlock.of("(byte) $L", b));
    }
    builder.add(CodeBlock.join(blocks, ", "));
    builder.add("}");
    return builder.build();
  }

  private Class<?> getResourceClass(ResourceType type) {
    return switch (type) {
      case STRING -> String.class;
      case BYTES -> byte[].class;
      case STREAM -> java.io.InputStream.class;
    };
  }

  private String getConversion(ResourceType type) {
    return switch (type) {
      case STRING -> "asString()";
      case BYTES -> "asBytes()";
      case STREAM -> "asStream()";
    };
  }

  private FieldSpec instanceField(String packageName, String className) {
    return FieldSpec.builder(ClassName.get(packageName, className), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("new $T()", ClassName.get(packageName, className))
      .build();
  }

  private MethodSpec constructor() {
    return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
  }

	@Override
	public List<CompiledResource> compile(Context context, ResourceBundleNode bundle) {
		TypeSpec.Builder resourceBuilder = TypeSpec.classBuilder(bundle.name()).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    boolean hasParent = switch (context.resourceElement()) {
      case InterfaceResourceElement ignored -> true;
      case PackageResourceElement ignored -> false;
    };

    resourceBuilder.addField(instanceField(context.resourceElement().packageName(), bundle.name()));
    resourceBuilder.addMethod(constructor());

    switch (context.resourceElement()) {
      case InterfaceResourceElement element -> resourceBuilder.addSuperinterface(ClassName.get(element.packageName(), element.name()));
      case PackageResourceElement ignored -> {}
    }

    List<MethodSpec.Builder> methods = new ArrayList<>();

		for (ResourceNode resource : bundle.resources()) {
      switch (resource.body()) {
        case InlineResourceBodyNode body -> methods.add(inline(resource, body));
        case FileResourceBodyNode body -> methods.add(file(resource, body));
      }
		}

    for (MethodSpec.Builder method : methods) {
      if (hasParent) {
        resourceBuilder.addMethod(method.addAnnotation(Override.class).build());
      } else {
        resourceBuilder.addMethod(method.build());
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
