package com.srilakshmikanthanp.resources;


import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    // With MainResource Interface
    for (MainResource resource : new MainResource[]{ Sample1Xml.INSTANCE, Sample1Yml.INSTANCE }) {
      System.out.println(resource.echo());
      try (var stream = resource.config()) {
        System.out.println(new String(stream.readAllBytes()));
      }
    }

    // Without Interface
    System.out.println(Sample2Xml.INSTANCE.echo());
    System.out.println(Sample2Yml.INSTANCE.echo());
	}
}
