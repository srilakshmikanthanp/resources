package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
@Repeatable(Resources.class)
public @interface Resource {
  CompilerType compiler();
  ParserType parser();
  String path();
}
