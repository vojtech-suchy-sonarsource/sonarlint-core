/*
ACR-87e781efbc9c4014973ba26dea57ad3f
ACR-1b4781917f7d4a1f8e4979dd36c612c2
ACR-50d5ee312ad04c51ad381e417b554670
ACR-84e844c9959240f88763336b98c91472
ACR-bf80d679b36f423fbf596d6074f3298e
ACR-c61cccd66c1c4a608d17986b5367879d
ACR-910ecb45663c4467adfd90255c3f6d82
ACR-f09011944a674c99b1f201e02f7fdd43
ACR-22e81b6f239a43ad9e732ba4ebbc588c
ACR-9d5a4cae68b244119a505f14c03c3901
ACR-27a82db80b3f4b5b9bf1720c149aebdb
ACR-68545ef5cf284ab08c8330637a10b966
ACR-8fdf73121811475583c3516d75653c9f
ACR-eb1dc3aa5fe04758852a152eb1ee4a06
ACR-ca2e1d3a648846389f4f88e0cf7143e5
ACR-cf6ebc454bfe4e4688d02c1bd190edb7
ACR-75c9fce6dd7d44e8a3d0369b4d1f0504
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
