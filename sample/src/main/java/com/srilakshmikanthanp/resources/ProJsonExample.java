package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

@Resource(path = "ProExampleJson.json", parser = ParserType.JSON_V1, compiler = CompilerType.JAVA_V1)
public interface ProJsonExample {
  String welcome(String user, boolean status);
  String discount(double percent, String item);
}
