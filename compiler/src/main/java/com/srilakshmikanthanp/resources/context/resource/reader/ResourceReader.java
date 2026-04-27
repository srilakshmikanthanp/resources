package com.srilakshmikanthanp.resources.context.resource.reader;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceReader {
  InputStream read(String packageName, String path) throws IOException;
}
