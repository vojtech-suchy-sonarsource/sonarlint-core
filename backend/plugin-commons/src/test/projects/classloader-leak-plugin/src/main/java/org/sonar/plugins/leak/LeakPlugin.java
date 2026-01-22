package org.sonar.plugins.leak;

import java.io.IOException;
import org.sonar.api.Plugin;

public class LeakPlugin implements Plugin {
  @Override
  public void define(Context context) {
    //ACR-ffd80484911b4990a9a2bb5dd316ea1a
    var resource = this.getClass().getClassLoader().getResource("Hello.txt");
    //ACR-518150c7f1be4a6a95090d3f265972e4
    try (var conn = resource.openConnection().getInputStream()) {
      conn.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    //ACR-a2a0753187fb4feb8e5d6b9ab42463b0
  }
}
