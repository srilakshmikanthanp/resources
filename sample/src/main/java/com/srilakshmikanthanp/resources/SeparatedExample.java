package com.srilakshmikanthanp.resources;


import java.io.IOException;

public class SeparatedExample {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(CommandExecutor.executeCommand(SeparatedExampleCommands.INSTANCE.helloWorld()));
    System.out.println(CommandExecutor.executeCommand(SeparatedExampleCommands.INSTANCE.listFiles()));
    System.out.println(CommandExecutor.executeCommand(SeparatedExampleCommands.INSTANCE.findGitProject()));
	}
}
