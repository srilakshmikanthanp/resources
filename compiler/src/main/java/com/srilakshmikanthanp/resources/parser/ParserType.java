package com.srilakshmikanthanp.resources.parser;

import com.srilakshmikanthanp.resources.parser.json.JsonResourceParserV1;
import com.srilakshmikanthanp.resources.parser.properties.PropertiesResourceParserV1;
import com.srilakshmikanthanp.resources.parser.xml.XmlResourceParserV1;
import com.srilakshmikanthanp.resources.parser.yml.YmlResourceParserV1;
import lombok.Getter;

@Getter
public enum ParserType {
  XML_V1(new XmlResourceParserV1()),
  YML_V1(new YmlResourceParserV1()),
  JSON_V1(new JsonResourceParserV1()),
  PROPERTIES_V1(new PropertiesResourceParserV1());

  private final ResourceParser parser;

  ParserType(ResourceParser parser) {
    this.parser = parser;
  }
}
