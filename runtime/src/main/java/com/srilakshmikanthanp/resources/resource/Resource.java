package com.srilakshmikanthanp.resources.resource;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Resource {
	/**
	 * Returns the resource as an InputStream. 
	 * The caller is responsible for closing 
	 * the stream.
	 * 
	 * @return the resource as an InputStream
	 */
	InputStream asStream();

	/**
	 * Returns the resource as a byte array.
	 * 
	 * @return the resource as a byte array
	 */
	byte[] asBytes();

	/**
	 * Returns the resource as a String.
	 * 
	 * @param charset the charset to
	 * use for decoding the bytes
	 * 
	 * @return the resource as a String
	 */
	String asString(Charset charset);

	/**
	 * Returns the resource as a String
	 * using the default charset.
	 * 
	 * @return the resource as a String
	 */
	default String asString() {
		return asString(StandardCharsets.UTF_8);
	}
}
