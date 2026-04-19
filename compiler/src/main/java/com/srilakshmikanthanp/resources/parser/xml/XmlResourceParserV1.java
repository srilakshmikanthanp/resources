package com.srilakshmikanthanp.resources.parser.xml;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceFieldType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class XmlResourceParserV1 implements ResourceParser {
  private static final String SCHEMA_RESOURCE_PATH = "resourse.xsd";
  private static final String NAME_ATTR = "name";
  private static final String INLINE_TAG = "inline";
  private static final String FILE_TAG = "file";
  private static final String PATH_ATTR = "path";
  private static final String TYPE_ATTR = "type";

  private ResourceBundleNode parseResourceBundleNode(Element element) {
    return new ResourceBundleNode(element.getAttribute(NAME_ATTR), this.parseResources(element));
  }

  private List<ResourceNode> parseResources(Element element) {
    List<ResourceNode> resources = new LinkedList<>();
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if (element.getChildNodes().item(i).getNodeType() == Element.ELEMENT_NODE) {
        resources.add(parseResourceNode((Element) element.getChildNodes().item(i)));
      }
    }
    return resources;
  }

  private ResourceNode parseResourceNode(Element element) {
    return new ResourceNode(element.getAttribute(NAME_ATTR), parseResourceBody(element));
  }

  private ResourceBodyNode parseResourceBody(Element node) {
    Element element = (Element) node.getElementsByTagName("*").item(0);
    if (element == null) {
      return new InlineResourceBodyNode(node.getTextContent());
    }
    if (element.getTagName().equals(FILE_TAG)) {
      ResourceFieldType type = element.hasAttribute(TYPE_ATTR) ? ResourceFieldType.valueOf(element.getAttribute(TYPE_ATTR)) : ResourceFieldType.STREAM;
      String filePath = element.getAttribute(PATH_ATTR);
      return new FileResourceBodyNode(filePath, type);
    } else if (element.getTagName().equals(INLINE_TAG)) {
      ResourceFieldType type = element.hasAttribute(TYPE_ATTR) ? ResourceFieldType.valueOf(element.getAttribute(TYPE_ATTR)) : ResourceFieldType.STRING;
      String content = element.getTextContent();
      return new InlineResourceBodyNode(content, type);
    } else {
      throw new ResourceParserException("Unsupported resource type: " + element.getTagName());
    }
  }

  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    try (InputStream schemaStream = XmlResourceParserV1.class.getResourceAsStream(SCHEMA_RESOURCE_PATH)) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaStream)));
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new XmlResourceParserErrorHandler());
      Document document = builder.parse(stream);
      document.getDocumentElement().normalize();
      return parseResourceBundleNode(document.getDocumentElement());
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ResourceParserException("Failed to parse XML resource", e);
    }
  }
}
