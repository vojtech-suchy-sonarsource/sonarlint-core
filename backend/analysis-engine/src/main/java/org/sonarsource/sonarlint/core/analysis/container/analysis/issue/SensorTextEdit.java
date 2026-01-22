/*
ACR-f3d165d3c6ca474cafe84443809b9d96
ACR-4faa3e358cd2441d8e8f509467201295
ACR-3860557f5d7b440781b69961c385a1ba
ACR-3bb965dd7fe34fe4861a807cb203d21d
ACR-b5db7dccf5264799b08841a7cff1ac0d
ACR-d64cd7260638427593fb4ce03e256ddc
ACR-57aa3f26ae15467589e81ac78b99eaae
ACR-983c6c56c77a4944bbd33daffd740e86
ACR-87ba71ac8f8b49edaf6ae69a7a9a8a87
ACR-62c9b5de54904081bdac0a6d0d2fb4bc
ACR-3d74d87eaa07442087aadc8a58f19806
ACR-b743219917e549308da0d2376a7a2fe5
ACR-bb2f32dad20544b4990ac8770cd27da3
ACR-cb085f8018c94fe29ada510ac7d7932f
ACR-fbe8d1232b9148e69ef5d8cc17b62ed2
ACR-aeea7a9d21e442f9ab73b5f51dad22c0
ACR-3222e269e47b46928291aa94d32faa9e
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.issue.fix.NewTextEdit;
import org.sonar.api.batch.sensor.issue.fix.TextEdit;

public class SensorTextEdit implements TextEdit, NewTextEdit, org.sonarsource.sonarlint.plugin.api.issue.NewTextEdit {
  private TextRange range;
  private String newText;

  @Override
  public SensorTextEdit at(TextRange range) {
    this.range = range;
    return this;
  }

  @Override
  public SensorTextEdit withNewText(String newText) {
    this.newText = newText;
    return this;
  }

  @Override
  public TextRange range() {
    return range;
  }

  @Override
  public String newText() {
    return newText;
  }
}
