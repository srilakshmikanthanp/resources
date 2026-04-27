package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

@Resource(path = "SeparatedExampleCommands.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
public interface SeparatedExampleCommands {
  SeparatedExampleCommands INSTANCE = SeparatedExampleLinuxCommands.INSTANCE;

  String helloWorld();
  String listFiles();
  String findGitProject();
}
