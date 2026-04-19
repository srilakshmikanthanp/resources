package com.srilakshmikanthanp.resources.compiler;

import java.util.List;

import com.srilakshmikanthanp.resources.context.Context;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;

public interface ResourceCompiler {
	List<CompiledResource> compile(Context context, ResourceBundleNode resourceBundle);
}
