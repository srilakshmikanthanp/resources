package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompiledResource;
import com.srilakshmikanthanp.resources.compiler.ResourceCompiler;
import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.preprocessor.ResourcePreprocessor;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Orchestrator {
  private final List<ResourcePreprocessor> preprocessors = new ArrayList<>();

  private ResourceBundleNode preprocess(Context context, ResourceBundleNode resourceBundle) {
    return preprocessors.stream().reduce(resourceBundle, (bundle, preprocessor) -> preprocessor.process(context, bundle), (a, b) -> b);
  }

  public void addPreprocessor(ResourcePreprocessor preprocessor) {
    preprocessors.add(preprocessor);
  }

  public boolean removePreprocessor(ResourcePreprocessor preprocessor) {
    return preprocessors.remove(preprocessor);
  }

  public List<CompiledResource> compile(Context context, ResourceParser parser, ResourceCompiler compiler, InputStream input) {
    return compiler.compile(context, preprocess(context, parser.parse(context, input)));
  }
}
