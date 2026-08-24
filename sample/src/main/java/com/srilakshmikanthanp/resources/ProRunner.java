package com.srilakshmikanthanp.resources;

public class ProRunner {
  public static void main(String[] args) {
    System.out.println("=== Testing Pro Level Properties Commands ===");
    System.out.println(ProPropertiesCommands.INSTANCE.greet("Aravind", 5));
    System.out.println(ProPropertiesCommands.INSTANCE.order(10042L, 299.99));

    System.out.println("\n=== Testing Pro Level JSON Commands ===");
    System.out.println(ProJsonCommands.INSTANCE.welcome("Developer", true));
    System.out.println(ProJsonCommands.INSTANCE.discount(15.5, "MacBook Pro"));
  }
}
