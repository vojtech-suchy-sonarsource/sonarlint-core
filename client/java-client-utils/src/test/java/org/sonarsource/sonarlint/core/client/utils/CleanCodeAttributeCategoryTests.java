/*
ACR-3366e0c160f04edf81c79b9b4db0f0be
ACR-8bbe94d229b147d29589198a741abdbf
ACR-5783396327b348a3932f85a989597a94
ACR-7c50cef017204717a171530f38bf73f0
ACR-7ee563364a264138b838993df8daf29e
ACR-8745c68af0d74e81a6bda731fd3f3ce8
ACR-0b2243a141b644f9b39622470abf9f54
ACR-ad3a426e8584452da7faf6692a3d07b7
ACR-6465c0f44ed04eb0931e3a972f1948fc
ACR-1ca5ecd14b57481ca93a33e4c831f9a1
ACR-34c0b73ce8f346fcbe652e04fd01f949
ACR-5681304b6c5a4dd8982e35f198662aa6
ACR-de476cf1bc224e9783acc3828818488c
ACR-18f15e5799434d038d73fe4b6c54a548
ACR-3b2ac8865ea04710ab1678842d1d63b6
ACR-85737e8fb5e44bdb8ca7b3ce0de55c4b
ACR-4a1038852c7f4e1ba91dd4d05205c05b
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanCodeAttributeCategoryTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttributeCategory.values()) {
      var converted = CleanCodeAttributeCategory.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

}