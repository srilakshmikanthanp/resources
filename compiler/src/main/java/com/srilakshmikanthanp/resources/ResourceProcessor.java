package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.context.resource.*;
import com.srilakshmikanthanp.resources.context.resource.reader.ResourceReader;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.preprocessor.inliner.FileBytesInliner;
import com.srilakshmikanthanp.resources.preprocessor.inliner.FileStringInliner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SupportedAnnotationTypes({
  "com.srilakshmikanthanp.resources.Resource",
  "com.srilakshmikanthanp.resources.Resources"
})
public class ResourceProcessor extends AbstractProcessor {
  private final Orchestrator orchestrator = new Orchestrator(new FileStringInliner(), new FileBytesInliner());

  private void process(Context context, String path, ResourceParser parser, ResourceCompiler compiler) throws IOException {
    try (InputStream inputStream = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, context.resourceElement().packageName(), path).openInputStream()) {
      this.save(orchestrator.compile(context, parser, compiler, inputStream));
    }
  }

  private void process(Context context, Element element) throws IOException {
    for (var resource : element.getAnnotationsByType(Resource.class)) {
      this.process(context, resource.path(), resource.parser().getParser(), resource.compiler().getCompiler());
    }
  }

  private void save(List<CompiledResource> resources) throws IOException {
    for (CompiledResource compiled : resources) {
      String className = String.format("%s.%s", compiled.packageName(), compiled.className());
      JavaFileObject javaFile = processingEnv.getFiler().createSourceFile(className);
      try (var writer = javaFile.openWriter()) {
        writer.write(compiled.content());
      }
    }
  }

  private ResourceElement getResourceElement(String packageName, Element element) {
    return switch (element) {
      case TypeElement typeElement when typeElement.getKind().isInterface() -> new InterfaceResourceElement(packageName, typeElement.getQualifiedName().toString());
      case PackageElement ignored -> new PackageResourceElement(packageName);
      default -> throw new IllegalArgumentException("Only interfaces and packages can be annotated with @Resource or @Resources");
    };
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    ResourceReader resourceReader = new FilerResourceReader(processingEnv.getFiler());
    List<Element> resources = new LinkedList<>();

    resources.addAll(roundEnv.getElementsAnnotatedWith(Resources.class));
    resources.addAll(roundEnv.getElementsAnnotatedWith(Resource.class));

    Elements elementUtils = processingEnv.getElementUtils();

    for (var resource : resources) {
      try {
        String packageName = elementUtils.getPackageOf(resource).getQualifiedName().toString();
        ResourceElement element = getResourceElement(packageName, resource);
        this.process(new Context(element, resourceReader), resource);
      } catch (IOException e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to process resource: " + e.getMessage(), resource);
      } catch (IllegalArgumentException e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), resource);
      }
    }

    return true;
  }
}
