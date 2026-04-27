# Resources

Resources is a Java annotation processor that allows you to generate Java classes from resource files.

## Usage

Define a resource file in your classpath, for example `sample1.xml`:

```xml
<resources name="Sample1Xml">
  <resource name="echo">echo "Hello, World"</resource>
</resources>
```

Above is equivalent to the following YML,

```yml
name: Sample1Yml

resources:
  echo: echo "Hello, World"
```

Then, annotate with `@Resource`

```java
@Resource(path = "sample1.xml", parser = ParserType.XML_V1, compiler = CompilerType.JAVA_V1)
@Resource(path = "sample1.yml", parser = ParserType.YML_V1, compiler = CompilerType.JAVA_V1)
public interface MainResource {
  String echo();
}
```

When you compile your project, the annotation processor will generate a
Java class for each resource file. For example, for the above resource
files, it will generate following classes:

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineStringResource;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;

public final class Sample1Xml implements com.srilakshmikanthanp.resources.SeparatedExampleCommands {
  public static final Sample1Xml INSTANCE = new Sample1Xml();

  private Sample1Xml() {
  }

  @Override
  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }
}
```

```java
package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.resource.FileResource;
import com.srilakshmikanthanp.resources.resource.InlineStringResource;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;

public final class Sample1Yml implements com.srilakshmikanthanp.resources.SeparatedExampleCommands {
  public static final Sample1Yml INSTANCE = new Sample1Yml();

  private Sample1Yml() {
  }
  
  @Override
  public String echo() {
    return new InlineResource("echo \"Hello, World\"").asString();
  }
}
```

Use generated classes

```java
public class Main {
  public static void main(String[] args) {
    System.out.println(Sample1Xml.INSTANCE.echo());
    System.out.println(Sample1Yml.INSTANCE.echo());
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

### Resource Types

Resources supports multiple return types for each resource. In most cases, you don’t need to
specify a type explicitly—it is inferred automatically based on how the resource is defined.

#### 1. `STRING`

* Returns resource content as `String`
* Default for inline resources

```xml
<resource name="helloWorld">
  echo "Hello, World!"
</resource>
```

---

#### 2. `STREAM`

* Returns resource as `InputStream`
* Default for file-based resources

```xml
<resource name="script">
  <file path="script.sh" />
</resource>
```

---

#### 3. `BYTES`

* Returns resource as `byte[]`
* Useful for binary or raw data access

```xml
<resource name="binaryFile" type="BYTES">
  <file path="data.bin" />
</resource>
```

---

#### Overriding Default Type

You can explicitly specify a type using the `type` attribute:

```xml
<resource name="findGitProject" type="STRING">
  <file path="FindGitProject.sh" />
</resource>
```

---

#### Optimization Behavior

If a file-based resource is declared as `STRING`:

```xml
<resource name="script" type="STRING">
  <file path="script.sh" />
</resource>
```

Or, If a file-based resource is declared as `BYTES`:

```xml
<resource name="script" type="BYTES">
  <file path="file.bin" />
</resource>
```

The processor will:

* Read the file at compile time
* Inline its content as a `String` or `byte[]` in the generated code
* Generate code similar to an inline resource

Benefits

* No runtime file access
* Better performance
* Same behavior as inline resources
