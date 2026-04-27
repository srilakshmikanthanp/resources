package com.srilakshmikanthanp.resources.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class InlineStringResource implements InlineResource {
	private final String content;

	public InlineStringResource(String content) {
		this.content = content;
	}

  @Override
  public String asString() {
    return content;
  }

  @Override
  public byte[] asBytes() {
    return content.getBytes();
  }

  @Override
	public InputStream asStream() {
		return new ByteArrayInputStream(content.getBytes());
	}
}
