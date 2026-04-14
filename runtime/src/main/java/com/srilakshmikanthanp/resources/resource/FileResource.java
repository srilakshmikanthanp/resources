package com.srilakshmikanthanp.resources.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class FileResource extends AbstractResource {
	private final Path path;

	public FileResource(String path) {
		this.path = Path.of(path);
	}

	public FileResource(Path path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return path.toString();
	}

	@Override
	public InputStream asStream() {
		try {
			return new FileInputStream(path.toFile());
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}
}
