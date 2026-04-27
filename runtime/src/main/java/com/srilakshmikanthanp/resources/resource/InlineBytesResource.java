package com.srilakshmikanthanp.resources.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class InlineBytesResource implements InlineResource {
	private final byte[] content;

	public InlineBytesResource(byte[] content) {
		this.content = content;
	}

  @Override
  public byte[] asBytes() {
    return content;
  }

  @Override
	public InputStream asStream() {
		return new ByteArrayInputStream(content);
	}
}
