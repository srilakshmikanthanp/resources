package com.srilakshmikanthanp.resources;

import java.io.IOException;

public class CommandExecutor {
  public static String executeCommand(String command) throws IOException, InterruptedException {
    Process process = new ProcessBuilder("sh", "-c", command).start();
    process.waitFor();
    return new String(process.getInputStream().readAllBytes());
  }
}
