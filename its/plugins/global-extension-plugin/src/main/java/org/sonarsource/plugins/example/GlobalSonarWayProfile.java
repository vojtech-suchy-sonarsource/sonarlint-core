/*
ACR-9efca3e973c44ad8aa70716dc786e133
ACR-7603176815aa4d3fb99c21f1b6a0b434
ACR-223b0e87c5154789b3892e8be4c8428c
ACR-eeb73663c6634df0b6337afe561bb7d0
ACR-60605987f11e4508bb6365bba46d5b0f
ACR-6d4fedb8fda746b2af3442b93831a3aa
ACR-752f3a36f74a40d2a413c658232b7666
ACR-d4792f3985ed4e72acc561af8d429ea8
ACR-c56ee890d2ce40adb5616a9045866dc1
ACR-978b4f05993d42f28c8796ec2c040c31
ACR-abf745934e5445a7a11ccd3782ba7eb5
ACR-1d25c711aaa143a4bae2c248d4224868
ACR-67c17cd37ad84a5f888643ace0aab78c
ACR-7509b00f1497417eae48e11843403167
ACR-89a217b790024ab4af9caf14f047c852
ACR-2648eb4efdab4c1184642fe6e6eaf25e
ACR-1ec6505e5b9b418b88128ffe910f0413
 */
package org.sonarsource.plugins.example;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public final class GlobalSonarWayProfile implements BuiltInQualityProfilesDefinition {

  @Override
  public void define(Context context) {
    context.createBuiltInQualityProfile("Sonar Way", GlobalLanguage.LANGUAGE_KEY)
      .setDefault(true)
      .done();
  }

}
