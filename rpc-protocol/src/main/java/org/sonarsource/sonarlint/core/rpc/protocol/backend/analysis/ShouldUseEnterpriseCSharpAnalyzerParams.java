/*
ACR-691e80725de247c88b7f02fa6389cff3
ACR-1cfebef1b48e47e786a0f1c02149bb7c
ACR-4420d4edcfb243ab9a88e1820fd96b1f
ACR-450d090b0e5b4fb4aef3c611105987ba
ACR-75ece8d5643345739641bda1252cae68
ACR-a81e9dfa06774d25ab5821ff01e5dfa1
ACR-c91b3cf98f8a49d197c9a5b06664e14d
ACR-612443ddc0304fcda39782bb5146370d
ACR-1d4a1b75ed1546959c7be6dedce8e5e4
ACR-e12d9a745f854dc2948b235996f3d3c2
ACR-922c83d98f934526804b35ca34efffdb
ACR-cfd61fa1a6f64c128ae83f35a6fc6f54
ACR-3ee96d6262b448b4abcb76e65e3bccb1
ACR-6af4df00b9a04bd4b5572b57681ac8f9
ACR-0ea4687ab6d5427bb2a5d33afaeeec7f
ACR-059f042dc0414cd6a1a818503435d3d5
ACR-c5ae575b3be143c8871b840aee3de547
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class ShouldUseEnterpriseCSharpAnalyzerParams {
  private final String configurationScopeId;

  public ShouldUseEnterpriseCSharpAnalyzerParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
