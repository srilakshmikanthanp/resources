package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

@Resource(path = "ProExampleProperties.properties", parser = ParserType.PROPERTIES_V1, compiler = CompilerType.JAVA_V1)
public interface ProPropertiesExample {
  String greet(String name, int count);
  String order(long id, double amount);
}
