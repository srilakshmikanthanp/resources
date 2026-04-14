package com.srilakshmikanthanp.resources.compiler;

import com.srilakshmikanthanp.resources.compiler.java.v1.JavaResourceCompilerV1;

public enum CompilerType {
	JAVA_V1(new JavaResourceCompilerV1());

	private final ResourceCompiler compiler;

	CompilerType(ResourceCompiler compiler) {
		this.compiler = compiler;
	}

	public ResourceCompiler getCompiler() {
		return this.compiler;
	}
}
