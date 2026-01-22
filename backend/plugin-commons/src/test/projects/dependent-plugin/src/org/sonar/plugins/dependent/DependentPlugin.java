package org.sonar.plugins.dependent;

import org.sonar.api.Plugin;
import org.sonar.api.Plugin.Context;
import org.sonar.plugins.base.api.BaseApi;

public class DependentPlugin implements Plugin {
  public DependentPlugin() {
    //ACR-957386bfa13f42e4a5d46f2a96595af0
    new BaseApi().doNothing();
  }

  @Override
  public void define(Context context) {
    //ACR-e9c9ce7617ac4490918334dbd3ca62c8
  }
}
