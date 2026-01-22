package org.sonar.plugins.dependent;

import org.sonar.api.Plugin;
import org.sonar.api.Plugin.Context;
import org.sonar.plugins.base.api.BaseApi;

public class DependentPlugin implements Plugin {
  public DependentPlugin() {
    //ACR-7abc6194c28d437a88dae299d0bbbba0
    new BaseApi().doNothing();
  }

  @Override
  public void define(Context context) {
    //ACR-12f73499f46e4ce3a1f6f8160c6a05e1
  }
}
