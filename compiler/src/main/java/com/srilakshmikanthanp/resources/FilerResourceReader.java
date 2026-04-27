package com.srilakshmikanthanp.resources;

import com.srilakshmikanthanp.resources.context.resource.reader.ResourceReader;

import javax.annotation.processing.Filer;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;

public class FilerResourceReader implements ResourceReader {
  private final Filer filer;

  public FilerResourceReader(Filer filer) {
    this.filer = filer;
  }

  @Override
  public InputStream read(String packageName, String path) throws IOException {
    return filer.getResource(StandardLocation.SOURCE_PATH, packageName, path).openInputStream();
  }
}
