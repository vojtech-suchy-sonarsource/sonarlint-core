package org.sonar.plugins.leak;

import java.io.IOException;
import org.sonar.api.Plugin;

public class LeakPlugin implements Plugin {
  @Override
  public void define(Context context) {
    //ACR-3a2d9ba8477642dfa3e229a2addb8c19
    var resource = this.getClass().getClassLoader().getResource("Hello.txt");
    //ACR-e91c46f1dcd14b7db5ce00c63fb4dc2c
    try (var conn = resource.openConnection().getInputStream()) {
      conn.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    //ACR-a67f29274be24bceaeea12a337645855
  }
}
