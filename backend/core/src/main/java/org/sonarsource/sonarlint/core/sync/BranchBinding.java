/*
ACR-8a5fe2fdf7614eedb64b2e80ff6fae95
ACR-7d95cb11eca241959be990757910daa0
ACR-f88289762fe64834b41d9ffe7c128d1d
ACR-135acd80d345476bbe2cdeb181cf07b2
ACR-009acb2eaeda4492ab95b902aa2d8f03
ACR-811c5ccb2ee24982bd313b99a84379ff
ACR-44ad32395cc94b24bf3aa1ac3231c6ae
ACR-1e55258d933542f1aff0c0b51389bf3f
ACR-698d6a09ffe344db96e1155ac8ea098f
ACR-8a4a43fa1ad24a828b31eea8f4eacac7
ACR-998627fe9f464ed1aa0665d724be3d4d
ACR-4eeef00dff5d403292e7ba815ec8b7a0
ACR-26bda431cbcf4a3480af0bf73a76758a
ACR-387b3c7773354333907cfd105e91f349
ACR-a4643c0d9d1847b7abd02afc8cc72b02
ACR-0bed688987784a8d90957367730434c8
ACR-8aec0e7ff7714c38a7a22ff7fedce473
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
