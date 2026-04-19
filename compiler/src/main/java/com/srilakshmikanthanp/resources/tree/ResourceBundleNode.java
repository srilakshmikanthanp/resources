package com.srilakshmikanthanp.resources.tree;

import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;

import java.util.List;

public record ResourceBundleNode(String name, List<ResourceNode> resources) {}
