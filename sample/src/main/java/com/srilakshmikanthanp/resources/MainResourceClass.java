package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

import java.io.InputStream;

@Resource(path = "sample3.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample3.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
public abstract class MainResourceClass {
  public abstract String echo();
  public abstract String print();
  public abstract InputStream config();
}
