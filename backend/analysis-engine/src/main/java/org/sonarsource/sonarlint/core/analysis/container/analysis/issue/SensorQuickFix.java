/*
ACR-d3e5eebd9c6d4512a2d27fd6fdeb3701
ACR-8b83860782ea4dd390f8843d13963aa8
ACR-46188891027a4caf8201e8823ea73bb2
ACR-51ad51506f0d48f3b3b83ccf0a4c8e1c
ACR-54b0156cb6744c4db656880847c22c45
ACR-9f3a3f282bcc4d319c369d172ac69f6e
ACR-faab68debf4e4423bc7e64452125bc89
ACR-03df987ba9394d1bb725cc81da473544
ACR-a2c334ac61fe4382a8bb4a2c0dfa2a91
ACR-06c282a29b3d40e7a894f17c290a9cd5
ACR-36adba93a8b041e9ad551c2b223cb8ac
ACR-6362e43439ce454a85940edda87993d8
ACR-bf0c2bfae9ab4771b53100d2c75bdf14
ACR-fc42f69462c64f8f840baa56058d14ae
ACR-d89baa34f50b430bbed8e647b72a0ee7
ACR-42f02e598314492f9ef0030fa5f293ee
ACR-9890737662464a81a396c554a80f8ea7
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
    //ACR-9daaac242e77470483cad89a657689f1
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
