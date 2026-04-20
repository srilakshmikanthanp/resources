# Resources

Resources is a Java annotation processor that allows you to generate Java classes from resource files.

## Usage

Define a resource file in your classpath, for example `sample1.xml`:

```xml
<resources name="Sample1Xml">
  <resource name="echo">
    <inline>echo "Hello, World"</inline>
  </resource>

  <!-- Inline is default, so it can be omitted -->
  <resource name="print">
    echo "Hello, World"
  </resource>

  <resource name="config">
    <file path="sample1.xml"/>
  </resource>
</resources>
```

Above is equivalent to the following YML,

```yml
name: Sample1Yml

resources:
  # Inline is default, so it can be omitted
  print: echo "Hello, World"

  config:
    file: sample1.yml

  echo:
    inline: echo "Hello, World"
```

Then, annotate with `@Resource`

```java
@Resource(path = "sample1.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample1.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
public interface MainResource {
  String print();
  String echo();
  InputStream config();
}
```

When you compile your project, the annotation processor will generate a
Java class for each resource file. For example, for the above resource
files, it will generate following classes:

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineResource;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;

public final class Sample1Xml implements com.srilakshmikanthanp.resources.MainResource {
  public static final Sample1Xml INSTANCE = new Sample1Xml();

  private Sample1Xml() {
  }

  @Override
  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }

  @Override
  public String print() {
    return new InlineResource("\n"
      + "    echo \"Hello, World\"\n"
      + "  ").asString();
  }

  @Override
  public InputStream config() {
    return new FileResource(getClass().getResource("sample1.xml").getPath()).asStream();
  }
}
```

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineResource;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;

public final class Sample1Yml implements com.srilakshmikanthanp.resources.MainResource {
  public static final Sample1Yml INSTANCE = new Sample1Yml();

  private Sample1Yml() {
  }

  @Override
  public String print() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }

  @Override
  public InputStream config() {
    return new FileResource(getClass().getResource("sample1.yml").getPath()).asStream();
  }

  @Override
  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }
}
```

Interface is optional. If you don't need it, annotate in `@Resource` in `package-info.java` file
the generated class will not implement any interface. But it is good for decoupling.

```java
@Resource(path = "sample2.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample2.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.compiler.CompilerType;
import com.srilakshmikanthanp.resources.parser.ParserType;
```

For more details, please refer to the [sample](./sample) module.