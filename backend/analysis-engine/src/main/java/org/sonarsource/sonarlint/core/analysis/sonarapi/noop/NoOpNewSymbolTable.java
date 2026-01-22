/*
ACR-7987a41dbaa14a2dabb8c5d2ca3b5927
ACR-e835d1491bdf4fab961ddaf9c3ca7a24
ACR-3ad0be5f7bf34b9a9d2396406c55eff5
ACR-34a75a88964b460d997ec26defde70c1
ACR-a60538d4083343aeb30e3daaf44dcfae
ACR-fc9e10bb99524576bdcff2f38e240b6c
ACR-a06fadff52674b7da7dde3d36ed7e5a4
ACR-e1a817c8403f48739c14d684d63d05e4
ACR-9d9c10d91ece49ea83ba2525abd4be5f
ACR-4fe0b7cef5c2457797798c125aa294cf
ACR-74dfabeade8046b79a016f5d8cdacd44
ACR-6720e32feca94ed39d53d52ff49047e6
ACR-88b1235e733648a3865d91efa0308ccb
ACR-042a4e801c50461daace4673bf15bc93
ACR-a520b9488fdc419dbdbff05f650ba45b
ACR-3769a9a1656840928c1a0b9b2d0b8c63
ACR-3c09c559c6d84191b71b7ef1eabaaf35
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.symbol.NewSymbol;
import org.sonar.api.batch.sensor.symbol.NewSymbolTable;

public class NoOpNewSymbolTable implements NewSymbolTable, NewSymbol {
  @Override
  public void save() {
    //ACR-ceb5b30814c8411ebef889f937a18f22
  }

  @Override
  public NoOpNewSymbolTable onFile(InputFile inputFile) {
    //ACR-0d992a76400f42df90de080a9e299478
    return this;
  }

  @Override
  public NoOpNewSymbolTable newSymbol(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    //ACR-b8837bdda82349289bbde3bdcc61b348
    return this;
  }

  @Override
  public NoOpNewSymbolTable newSymbol(TextRange range) {
    //ACR-a209ec8dd1ef421487ec2a28bed7d9af
    return this;
  }

  @Override
  public NoOpNewSymbolTable newReference(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    //ACR-51683eded6894e87a55fcd8254a0c368
    return this;
  }

  @Override
  public NoOpNewSymbolTable newReference(TextRange range) {
    //ACR-ff5b7a5f85e840a6b75b1c8344bbc22a
    return this;
  }

}
