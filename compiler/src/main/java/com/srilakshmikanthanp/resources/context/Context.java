package com.srilakshmikanthanp.resources.context;

import com.srilakshmikanthanp.resources.context.resource.ResourceElement;
import com.srilakshmikanthanp.resources.context.resource.reader.ResourceReader;

public record Context(ResourceElement resourceElement, ResourceReader resourceReader) {}
