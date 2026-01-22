/*
ACR-fd833aaff47849279f526156a6032f6d
ACR-df8cdbd6a7614b5e8c332de35ff86cfc
ACR-55c2270dc60a4d00995843507a4a4dfc
ACR-02372800b9994dc28256ff5ddd617be1
ACR-df3f6a629f85498292affd9a56c69204
ACR-efed867a782c4d3e89c3a30b6eb3f3e8
ACR-e0ded6c6006946f0b8345fe900d1cc40
ACR-eea2e6c243c8423bbe99bf413703fc6f
ACR-4d7baaf0801e49509829e6208052b8f7
ACR-92aafefd9cdf433296753d4caaa9a1bc
ACR-5813b706f01e43468067e02cbcdcbdd9
ACR-c57b5aa8c9f145a49bcde0b25998f819
ACR-7119454811df4a8f8bbf52f1356f01f2
ACR-23bf9375132e479788b791dd45758371
ACR-70f1d5c2f51848fa86bc72b765fc3be6
ACR-844563c101fa475597ee1f30a7955503
ACR-bb8a0769aade4bf2ae89e32e1f5d29f8
 */
package org.sonarsource.sonarlint.core.sync;

import java.util.Objects;
import org.sonarsource.sonarlint.core.commons.Binding;

public class BranchBinding {
  private final Binding binding;
  private final String branchName;

  public BranchBinding(Binding binding, String branchName) {
    this.binding = binding;
    this.branchName = branchName;
  }

  public Binding getBinding() {
    return binding;
  }

  public String getBranchName() {
    return branchName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BranchBinding that = (BranchBinding) o;
    return Objects.equals(binding, that.binding) && Objects.equals(branchName, that.branchName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(binding, branchName);
  }
}
