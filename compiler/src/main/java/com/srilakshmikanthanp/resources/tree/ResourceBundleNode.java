package com.srilakshmikanthanp.resources.tree;

import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ResourceBundleNode {
  private final String packageName;
  private final String name;
  private final String implementing;
  private final List<ResourceNode> resources = new LinkedList<>();

  public ResourceBundleNode(String packageName, String name, String implementing) {
    this.packageName = packageName;
    this.name = name;
    this.implementing = implementing;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getImplementing() {
    return Optional.ofNullable(implementing);
  }

  public void addResource(ResourceNode entry) {
    this.resources.add(entry);
  }

  public List<ResourceNode> getResources() {
    return resources;
  }
}
