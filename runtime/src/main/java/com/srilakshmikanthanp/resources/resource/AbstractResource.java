package com.srilakshmikanthanp.resources.resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

public interface AbstractResource extends Resource {
	@Override
	default public String asString(Charset charset) {
		return new String(asBytes(), charset);
	}

	@Override
  default public byte[] asBytes() {
		try (var inputStream = asStream()) {
			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
