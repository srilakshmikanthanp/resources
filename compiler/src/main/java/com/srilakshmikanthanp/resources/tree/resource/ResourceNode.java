package com.srilakshmikanthanp.resources.tree.resource;

import com.srilakshmikanthanp.resources.tree.resource.body.ResourceBodyNode;

public class ResourceNode {
	private final String name;
	private final ResourceBodyNode body;

	public ResourceNode(String name, ResourceBodyNode body) {
		this.name = name;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public ResourceBodyNode getBody() {
		return body;
	}
}
