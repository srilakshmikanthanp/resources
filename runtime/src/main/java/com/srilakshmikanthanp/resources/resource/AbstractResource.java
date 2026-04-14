package com.srilakshmikanthanp.resources.resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

public abstract class AbstractResource implements Resource {
	@Override
	public String asString(Charset charset) {
		return new String(asBytes(), charset);
	}

	@Override
	public byte[] asBytes() {
		try (var inputStream = asStream()) {
			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String toString() {
		return asString();
	}
}
