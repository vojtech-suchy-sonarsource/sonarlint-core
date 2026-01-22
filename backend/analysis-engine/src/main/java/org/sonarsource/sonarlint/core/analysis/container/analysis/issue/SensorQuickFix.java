/*
ACR-39c82f695bac49c28c0549c570a20420
ACR-518f0cd114ed4affb0902739a1f0a959
ACR-02249a2fc77a48939cfa620e29f0f2eb
ACR-edc4a10b91b74ce4aedc297a0ec52882
ACR-1bb9c76138b142189f9f2d497aeb99eb
ACR-4bbb769ff06c4bb38e04bcb7aaebfe72
ACR-7d0c13a4658b47d4ba7533a6b8122afb
ACR-3cbcb6e2a38044338a024fc8f224ca09
ACR-3b7480848d02432e876dbfaa486e7c43
ACR-e08ea7c2a67b498c899ccab8db0cc2af
ACR-4c24e82076044345bd6214f281fec1c3
ACR-693a47a02ff346c28b1cf7a7f3707449
ACR-f23a2c9a1d9249e2b4a6e87dea0e8911
ACR-ebd75f92baa44371afc31423b8eadbbc
ACR-293ed84025ce41e0810d9d71e5a46806
ACR-4664567d53284ee5bdea95a7d3922147
ACR-dbeaf10d3e2a4ae2b2088d6a36eff1c6
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import java.util.ArrayList;
import java.util.List;
import org.sonar.api.batch.sensor.issue.fix.InputFileEdit;
import org.sonar.api.batch.sensor.issue.fix.NewInputFileEdit;
import org.sonar.api.batch.sensor.issue.fix.NewQuickFix;
import org.sonar.api.batch.sensor.issue.fix.QuickFix;

public class SensorQuickFix implements QuickFix, NewQuickFix, org.sonarsource.sonarlint.plugin.api.issue.NewQuickFix {

  private final List<InputFileEdit> inputFileEdits = new ArrayList<>();

  private String message;

  @Override
  public SensorQuickFix message(String message) {
    this.message = message;
    return this;
  }

  @Override
  public SensorInputFileEdit newInputFileEdit() {
    return new SensorInputFileEdit();
  }

  @Override
  public NewQuickFix addInputFileEdit(NewInputFileEdit newInputFileEdit) {
    inputFileEdits.add((SensorInputFileEdit) newInputFileEdit);
    return this;
  }

  @Override
  public SensorQuickFix addInputFileEdit(org.sonarsource.sonarlint.plugin.api.issue.NewInputFileEdit newInputFileEdit) {
    //ACR-ce1d4ac5303c4409a6297d5e4c0c7377
    inputFileEdits.add((SensorInputFileEdit) newInputFileEdit);
    return this;
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  public List<InputFileEdit> inputFileEdits() {
    return inputFileEdits;
  }
}
