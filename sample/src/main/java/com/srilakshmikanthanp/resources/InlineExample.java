package com.srilakshmikanthanp.resources;

import java.io.IOException;

public class InlineExample {
  private static final String FIND_GIT_PROJECT = """
    echo "== Git Project Check =="
    
    DIR="$PWD"
    
    while [ "$DIR" != "/" ]; do
      if [ -d "$DIR/.git" ]; then
        echo "Git repository found at: $DIR"
        cd "$DIR"
        git status
        exit 0
      fi
      DIR="$(dirname "$DIR")"
    done
    
    echo "No git repository found in current or parent directories"
    
    echo "== Done =="
    """;

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(CommandExecutor.executeCommand("echo \"Hello World\""));
    System.out.println(CommandExecutor.executeCommand("ls -l"));
    System.out.println(CommandExecutor.executeCommand(FIND_GIT_PROJECT));
  }
}
