package com.srilakshmikanthanp.resources;


import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    // With MainResource type
    for (MainResource resource : new MainResource[]{ Sample1Xml.INSTANCE, Sample1Yml.INSTANCE }) {
      try (var stream = resource.config()) {
        System.out.println(new String(stream.readAllBytes()));
      }
      System.out.println(resource.echo());
      System.out.println(resource.print());
    }

    // Without Interface
    System.out.println(Sample2Xml.INSTANCE.echo());
    System.out.println(Sample2Yml.INSTANCE.echo());
	}
}
