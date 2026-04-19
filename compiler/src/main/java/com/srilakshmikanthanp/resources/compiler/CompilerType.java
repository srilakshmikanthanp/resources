package com.srilakshmikanthanp.resources.compiler;

import com.srilakshmikanthanp.resources.compiler.java.v1.JavaResourceCompilerV1;
import lombok.Getter;

@Getter
public enum CompilerType {
	JAVA_V1(new JavaResourceCompilerV1());

	private final ResourceCompiler compiler;

	CompilerType(ResourceCompiler compiler) {
		this.compiler = compiler;
	}
}
