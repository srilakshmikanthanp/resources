# Resources

Resources is a Java annotation processor that allows you to generate Java classes from resource files.

## Usage

Define a resource file in your classpath, for example `sample1.xml`:

```xml
<resources package="com.srilakshmikanthanp.resources" name="Sample1Xml" implementing="com.srilakshmikanthanp.resources.MainResource">
  <resource name="echo">
    <inline>echo "Hello, World"</inline>
  </resource>

  <resource name="config">
    <file path="config.xml"/>
  </resource>
</resources>
```

Above is equivalent to the following YML,

```yml
package: com.srilakshmikanthanp.resources
name: Sample1Yml
implementing: com.srilakshmikanthanp.resources.MainResource

resources:
  config:
    file: config.yml

  echo:
    inline: echo "Hello, World"
```

Then, annotate a package with `@Resource`

```java
// package-info.java
@Resource(path = "sample1.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample2.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
package com.srilakshmikanthanp.resources;
```

When you compile your project, the annotation processor will generate a
Java class for each resource file. For example, for the above resource
files, it will generate following classes:

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineResource;
import java.io.InputStream;
import java.lang.String;

public final class Sample1Xml implements com.srilakshmikanthanp.resources.MainResource {
  public static final Sample1Xml INSTANCE = new Sample1Xml();

  private Sample1Xml() {
  }

  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }

  public InputStream config() {
    return new FileResource(getClass().getResource("config.xml").getPath()).asStream();
  }
}
```

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineResource;
import java.io.InputStream;
import java.lang.String;

public final class Sample1Yml implements com.srilakshmikanthanp.resources.MainResource {
  public static final Sample1Yml INSTANCE = new Sample1Yml();

  private Sample1Yml() {
  }

  public InputStream config() {
    return new FileResource(getClass().getResource("config.yml").getPath()).asStream();
  }

  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }
}
```

Implementing interface in the resource file is optional. If you don't specify it, the generated class
will not implement any interface. But it is good for decoupling.
