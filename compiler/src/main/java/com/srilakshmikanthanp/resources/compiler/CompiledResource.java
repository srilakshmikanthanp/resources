package com.srilakshmikanthanp.resources.compiler;

public class CompiledResource {
	private final String packageName;
	private final String className;
	private final String content;

	public CompiledResource(String packageName, String className, String content) {
		this.packageName = packageName;
		this.className = className;
		this.content = content;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public String getContent() {
		return content;
	}
}
