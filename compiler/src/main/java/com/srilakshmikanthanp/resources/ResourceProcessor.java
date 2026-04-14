package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.parser.ResourceParser;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({
  "com.srilakshmikanthanp.resources.Resource",
  "com.srilakshmikanthanp.resources.Resources"
})
public class ResourceProcessor extends AbstractProcessor {
  private final Orchestrator orchestrator = new Orchestrator();

  private void process(PackageElement packageElement) throws IOException {
    for (var resource : packageElement.getAnnotationsByType(Resource.class)) {
      this.process(packageElement.getQualifiedName().toString(), resource.path(), resource.parser().getParser(), resource.compiler().getCompiler());
    }
  }

  private void process(String packageName, String path, ResourceParser parser, ResourceCompiler compiler) throws IOException {
    try (InputStream inputStream = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, packageName, path).openInputStream()) {
      this.save(orchestrator.compile(parser, compiler, inputStream));
    }
  }

  private void save(List<CompiledResource> resources) throws IOException {
    for (CompiledResource compiled : resources) {
      String className = String.format("%s.%s", compiled.getPackageName(), compiled.getClassName());
      JavaFileObject javaFile = processingEnv.getFiler().createSourceFile(className);
      try (var writer = javaFile.openWriter()) {
        writer.write(compiled.getContent());
      }
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<PackageElement> packages = new HashSet<>();

    for (var element : roundEnv.getElementsAnnotatedWith(Resources.class)) {
      if (element instanceof PackageElement) {
        packages.add((PackageElement) element);
      }
    }

    for (var element : roundEnv.getElementsAnnotatedWith(Resource.class)) {
      if (element instanceof PackageElement) {
        packages.add((PackageElement) element);
      }
    }

    for (var element : packages) {
      try {
        this.process(element);
      } catch (IOException e) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Failed to process resource: " + e.getMessage(), element);
      }
    }

    return true;
  }
}
