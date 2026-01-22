/*
ACR-5219ac27cba44a1eaa2ee8db0a8e19da
ACR-c83f6def21454e458e5dd137e4329ea0
ACR-b9994f79913548baad0ae7200adaf66e
ACR-383830b4b3124f88831f226e6f3bfd43
ACR-0683d74eb85143e6860ae71901c85655
ACR-d70f4b3243ff408c9f149be06604d672
ACR-73006db3cf8141bcab6373fa37256ad3
ACR-725aeb3dd3104cf7b976df2abf324f34
ACR-950f9c516f3f4191a3d5834bd96fc6b9
ACR-c777c6ba12b64de09a6d84f81f55480b
ACR-b5918ef40ed94647a98801e200696659
ACR-a02acd8cefc84929babd94050b6afd81
ACR-25064f4a94cb4c07bc5801c3d6822964
ACR-1913d0dd09d64d5ba53072dda7184ea5
ACR-b3a9001ced944bbdbbd1e205db3dfa59
ACR-4f73a5b49d234a019c19c7489f28773a
ACR-e3071723acae485abbeced746438be62
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;

public class IssueSeverityBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return IssueSeverity.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final IssueSeverity cPair = (IssueSeverity) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80000000);
  }

}
