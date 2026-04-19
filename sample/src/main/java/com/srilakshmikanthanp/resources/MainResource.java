package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

import java.io.InputStream;

@Resource(path = "sample1.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample1.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
public interface MainResource {
  String echo();
  String print();
  InputStream config();
}
