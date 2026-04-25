package com.srilakshmikanthanp.resources.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class InlineResource extends AbstractResource {
	private final String content;

	public InlineResource(String content) {
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
