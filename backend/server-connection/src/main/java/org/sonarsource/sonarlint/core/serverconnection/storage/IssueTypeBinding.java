/*
ACR-aadac51e6e21427fb217b99fc4f03fdf
ACR-7dde5376b2ea49ab9f75c9946bbae081
ACR-c9adf93ef9134472b61fe3275c165c33
ACR-6d13a5ec68cf48ee825bb44119a4f756
ACR-d3552d9731294bc8812973aa8e34ced7
ACR-c7a9c6c997144f7899744ce6134052d5
ACR-6ba43b780bb64c328c4c5949960ec214
ACR-d1511c7c2fc04987af4abf535d98d184
ACR-bb25802ff5bf41ffb0cfdd74caefd8ca
ACR-50eba0412ede41f6810592ae11eb56c9
ACR-0f0e8cb44031422c9f66e0888bc18812
ACR-7186189249604dc9a8d6435f73c2216b
ACR-86d5b575d04643348c70dbd8259e4217
ACR-9a02579ba7f8440ba1db8c4c692d20d2
ACR-7e5ca9fa38934fcb9e5638a1c3df19f9
ACR-1fdaf3c40ded4b7e81b236d91eb712b7
ACR-dc92a1c134a6456eaa96cfbd3d157e0d
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.RuleType;

public class IssueTypeBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return RuleType.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final RuleType cPair = (RuleType) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80_000_000);
  }

}
