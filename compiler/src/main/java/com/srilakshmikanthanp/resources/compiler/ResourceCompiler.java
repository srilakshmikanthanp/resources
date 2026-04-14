package com.srilakshmikanthanp.resources.compiler;

import java.util.List;

import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

public interface ResourceCompiler {
	/**
	 * Generates a resource class based on the provided ResourceBundle
	 * and writes it to the specified output path.
	 * 
	 * @param resourceBundle the ResourceBundle containing the 
	 * resource entries
	 * 
	 * @return a list of GeneratedResource objects representing the 
	 * generated classes
	 */
	List<CompiledResource> compile(ResourceBundleNode resourceBundle);
}
