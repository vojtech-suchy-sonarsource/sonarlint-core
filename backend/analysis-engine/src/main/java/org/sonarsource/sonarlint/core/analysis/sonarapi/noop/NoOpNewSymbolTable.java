/*
ACR-57afab1d1d314376ab0f05d1dde92b4a
ACR-ab9e3f62c3944564b16118399c5e2cef
ACR-ab1704eea86d4ba29982d8bfc9b455df
ACR-bf327005c4a04dc3907030ab32c1cbfd
ACR-d66c859d19894bcca6d1888c8cd9fac9
ACR-458438ba23f24d93832f433b8059ab37
ACR-e6219d0961234a73bd8405fd88849aea
ACR-955fc372944b47969f1b6a9f392997b3
ACR-09ea4e0a18a54150bb837b0b196507f7
ACR-bfee3f8e9679412ba75b14d3e55e7a77
ACR-b10390d03cbe4a22a297cd4a13ee6f10
ACR-51da6d02d21d404b90d67f9bc19a3e29
ACR-e75fe11e480d46c29305d78c27f5e09c
ACR-102782262f64453fabcf965e084f9f70
ACR-33026e548d744dd18a51c6323fc2f31a
ACR-7df122274f204c48abe21a5dc168d2b4
ACR-f7b8709ae2c24f0bbfe79219864bac69
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.symbol.NewSymbol;
import org.sonar.api.batch.sensor.symbol.NewSymbolTable;

public class NoOpNewSymbolTable implements NewSymbolTable, NewSymbol {
  @Override
  public void save() {
    //ACR-a45bbdf5bc95446a9c44d2d728ba1304
  }

  @Override
  public NoOpNewSymbolTable onFile(InputFile inputFile) {
    //ACR-7d23967869ff4086a651379194c022a0
    return this;
  }

  @Override
  public NoOpNewSymbolTable newSymbol(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    //ACR-b38e582abb7a4de38897aca1342197e0
    return this;
  }

  @Override
  public NoOpNewSymbolTable newSymbol(TextRange range) {
    //ACR-ce29da5161bc47c9851eaba44c8b4345
    return this;
  }

  @Override
  public NoOpNewSymbolTable newReference(int startLine, int startLineOffset, int endLine, int endLineOffset) {
    //ACR-6cf446e608014fecaf3f3d3ba1a5d49b
    return this;
  }

  @Override
  public NoOpNewSymbolTable newReference(TextRange range) {
    //ACR-06ad124a51c042f6ae92632049f526ca
    return this;
  }

}
