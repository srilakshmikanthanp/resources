package com.srilakshmikanthanp.resources.resource;

public sealed interface InlineResource extends AbstractResource permits InlineBytesResource, InlineStringResource {
}
