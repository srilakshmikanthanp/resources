package com.srilakshmikanthanp.resources.tree.resource.body;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an inline string resource whose content contains one or more
 * {@code {paramName}} or {@code {paramName:type}} placeholders, e.g.
 * {@code "Hello, {name}!"} or {@code "Item count: {count:int}"}.
 *
 * <p>Unlike {@link InlineStringResourceBodyNode}, whose value is fixed at
 * compile time, the accessor generated for this node takes method
 * parameters matching distinct placeholders and formats the value at call time
 * using {@link String#format(String, Object...)}.
 */
public record TemplateStringResourceBodyNode(String format, List<ParamSpec> params) implements InlineResourceBodyNode {
  // Matches {name} or {name:type} placeholders, or an escaped {{ / }} for a literal brace.
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\w+)(?::(\\w+))?}|\\{\\{|}}");

  /**
   * Helper method returning the list of parameter names for compatibility.
   */
  public List<String> paramNames() {
    return params.stream().map(ParamSpec::name).toList();
  }

  /**
   * Scans {@code content} for {@code {name}} or {@code {name:type}} placeholders and builds the
   * appropriate body node.
   *
   * @param content the raw resource text as authored in the resource file
   * @return an {@link InlineStringResourceBodyNode} when {@code content} has
   *         no placeholders (the common case), otherwise a
   *         {@link TemplateStringResourceBodyNode}
   */
  public static InlineResourceBodyNode parse(String content) {
    Matcher matcher = PLACEHOLDER.matcher(content);
    List<ParamSpec> params = new ArrayList<>();
    StringBuilder format = new StringBuilder();
    int last = 0;

    while (matcher.find()) {
      format.append(escapePercent(content.substring(last, matcher.start())));
      String token = matcher.group();

      if (token.equals("{{")) {
        format.append('{');
      } else if (token.equals("}}")) {
        format.append('}');
      } else {
        String name = matcher.group(1);
        String type = matcher.group(2) != null ? matcher.group(2) : "String";

        int index = -1;
        for (int i = 0; i < params.size(); i++) {
          if (params.get(i).name().equals(name)) {
            index = i;
            break;
          }
        }

        if (index < 0) {
          index = params.size();
          params.add(new ParamSpec(name, type));
        }

        ParamSpec spec = params.get(index);
        format.append(spec.formatSpecifier(index + 1));
      }

      last = matcher.end();
    }

    format.append(escapePercent(content.substring(last)));

    if (params.isEmpty()) {
      return new InlineStringResourceBodyNode(content);
    }

    return new TemplateStringResourceBodyNode(format.toString(), List.copyOf(params));
  }

  private static String escapePercent(String text) {
    return text.replace("%", "%%");
  }
}
