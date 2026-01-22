/*
ACR-b6d5d6c850934b518cf2456d55401606
ACR-58fd10a4958044b6acadcd6b56261698
ACR-8d4b215387ed409999a21ca12dac6efb
ACR-06b2470ca4cb4fa3a0794ccdaa783e80
ACR-c4e1eb5edc4d4e1395a9b7f69c5d04d0
ACR-ca38ffc92268470cba7420e58631b1bf
ACR-6499985beb054946a4cfa05375326e69
ACR-d2bf9bb7743746c69b765441d59a9d90
ACR-c739c1faa83b4098bd171344557788bd
ACR-ce8347390d8a4f38aedc3a2000e24c5d
ACR-a18d14f19f004a019be84679e7623b58
ACR-32b4ad75316f4627af0a0146f2b5e472
ACR-c1d7fba072b04f5a919b07180f4b26d0
ACR-57b8a24622d24faf82fda34a3466e988
ACR-5dc17444c44446c2b9bd03885a8d1088
ACR-2c8e63ca68e148b192e6cc2851ec3b1f
ACR-d68028146b124e2ea7916a27436bf78b
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

public class BlockIssuePattern {
  private final String beginBlockRegexp;
  private final String endBlockRegexp;

  public BlockIssuePattern(String beginBlockRegexp, String endBlockRegexp) {
    this.beginBlockRegexp = beginBlockRegexp;
    this.endBlockRegexp = endBlockRegexp;
  }

  public String getBeginBlockRegexp() {
    return beginBlockRegexp;
  }

  public String getEndBlockRegexp() {
    return endBlockRegexp;
  }
}
