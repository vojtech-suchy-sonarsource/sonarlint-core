/*
ACR-bcc1d466739549d5bd5111aa2c59abc8
ACR-b876453e54e74ce69658875f8285b1dd
ACR-7b069632722d401aaa595962c85ada2c
ACR-af76dda73072492dae1fe17acde12d87
ACR-bdc3ecbb44ac4a18ae88881b3f6b4fd7
ACR-432449bf07504d86b461d2bb1582cdc7
ACR-2a29a1ba6e0143eba7e45eff849e3c94
ACR-257c0089dee343e29194c8f839cd1d54
ACR-326ed057e51d40aeb00b5c6abfeaa90b
ACR-fffeb23c274648b09b01e23a6afdc2aa
ACR-0989074f6e594f2285bbeccb8b6f66b4
ACR-18bf976a5d8649ec85e322c391b3446f
ACR-4536da7dbc134510b5164843427133f6
ACR-7feb83fc093146da8a7415242d19e64b
ACR-cc981833c2d94a6c87a4be1e8c2399af
ACR-fcc75f2605924d52a602c250faef5530
ACR-ae59d1fd786c44c8a355b902cc2d85aa
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
    //ACR-440ef88f247d4848b24351b351dffb95
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
