package com.srilakshmikanthanp.resources.parser.xml;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceType;
import com.srilakshmikanthanp.resources.tree.resource.body.FileResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.InlineResourceBodyNode;
import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;
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
import java.lang.reflect.UndeclaredThrowableException;
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
    ResourceBodyNode body = parseResourceBody(element);
    ResourceType type = element.hasAttribute(TYPE_ATTR) ? ResourceType.valueOf(element.getAttribute(TYPE_ATTR)) : ResourceType.infer(body);
    return new ResourceNode(element.getAttribute(NAME_ATTR), parseResourceBody(element), type);
  }

  private ResourceBodyNode parseResourceBody(Element node) {
    Element element = (Element) node.getElementsByTagName("*").item(0);
    if (element == null) {
      return new InlineResourceBodyNode(node.getTextContent());
    } else if (element.getTagName().equals(FILE_TAG)) {
      return new FileResourceBodyNode(element.getAttribute(PATH_ATTR));
    } else if (element.getTagName().equals(INLINE_TAG)) {
      return new InlineResourceBodyNode(element.getTextContent());
    } else {
      throw new IllegalStateException("This should not happen, the XML should have been validated against the schema");
    }
  }

  @Override
  public ResourceBundleNode parse(Context context, InputStream stream) {
    try (InputStream schemaStream = XmlResourceParserV1.class.getResourceAsStream(SCHEMA_RESOURCE_PATH)) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaStream)));
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new XmlResourceParserErrorHandler());
      Document document = builder.parse(stream);
      document.getDocumentElement().normalize();
      return parseResourceBundleNode(document.getDocumentElement());
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }
}
