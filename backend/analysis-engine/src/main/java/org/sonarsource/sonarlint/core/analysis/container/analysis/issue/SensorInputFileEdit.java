/*
ACR-810dcd53295a46b5bc42097f5d9f5f8d
ACR-b01f5e8dbee644bd82590ab949680da8
ACR-0c19e89ac7da4a48be6433930f46773d
ACR-9f08ed49eaf54f618230b415b158e3a4
ACR-b5a192a046bf421f9fc909cdc686ce78
ACR-6a0d52af5e874f3ca13b7910dcc088dd
ACR-651e08c0dadc485daa3321ed100fe501
ACR-58607f98a6cd4a64a10435dfbd858668
ACR-7bbea29e646940d3a2c1d7b507550a41
ACR-637378f8b6814afa8bffc5e6b6cf1f17
ACR-d9eef592c0344c98acc1ad390fc1ec04
ACR-57f24d17e19e4a20b2789bb995322b33
ACR-d6eef4bd86d34a80bd3422571122bfe2
ACR-5186e897f36146b0ba0cf1886a062dbe
ACR-6e320118170d4e7c85657ba3753413f4
ACR-2757d6bec971489e8be20a36071b997d
ACR-24b626db329d495a81ad3828c7de4565
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.issue.fix.InputFileEdit;
import org.sonar.api.batch.sensor.issue.fix.NewInputFileEdit;
import org.sonar.api.batch.sensor.issue.fix.NewTextEdit;
import org.sonar.api.batch.sensor.issue.fix.TextEdit;

public class SensorInputFileEdit implements InputFileEdit, NewInputFileEdit, org.sonarsource.sonarlint.plugin.api.issue.NewInputFileEdit {

  private final List<TextEdit> textEdits = new ArrayList<>();

  private InputFile inputFile;

  @Override
  public SensorInputFileEdit on(InputFile inputFile) {
    this.inputFile = inputFile;
    return this;
  }

  @Override
  public SensorTextEdit newTextEdit() {
    return new SensorTextEdit();
  }

  @Override
  public NewInputFileEdit addTextEdit(NewTextEdit newTextEdit) {
    textEdits.add((SensorTextEdit) newTextEdit);
    return this;
  }

  @Override
  public org.sonarsource.sonarlint.plugin.api.issue.NewInputFileEdit addTextEdit(org.sonarsource.sonarlint.plugin.api.issue.NewTextEdit newTextEdit) {
    //ACR-a498b619c65a42038b2851af1b5cecfe
    textEdits.add((SensorTextEdit) newTextEdit);
    return this;
  }

  @Override
  public InputFile target() {
    return inputFile;
  }

  @Override
  public List<TextEdit> textEdits() {
    return Collections.unmodifiableList(textEdits);
  }
}
