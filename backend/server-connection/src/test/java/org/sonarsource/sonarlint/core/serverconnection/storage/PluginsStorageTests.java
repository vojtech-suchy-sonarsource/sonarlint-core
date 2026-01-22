/*
ACR-06c5de36342241e1802395aff5bd4d0d
ACR-8b19af883bf640dda04233e713337d85
ACR-1a0fb38ea75a484da20be06279b8b74a
ACR-3277c45e325d411d85a9f34bb56f62d5
ACR-2280841f21d84952bd9dbb3c9505663c
ACR-84b211f46a934f8aa3b5152ebad7675a
ACR-fe7cb20c687c44e4b8c49fd050c23077
ACR-d3f71c7b1a184789a517160feb07938b
ACR-62ea563f140f46c088172da37b991e00
ACR-9b5e731dd48043db97d9f31ad3a84f1f
ACR-3f90b35a047a4f9893436c120b3806de
ACR-8cd644592a064ce39f5ff59a56d88e84
ACR-4d88f404fd024e61b5bc1d1d44924ff0
ACR-ff5d866bf5e744f888de89076e63b887
ACR-4281937f1dbb4dac8a14fcd1bd990218
ACR-52ace7fb8d7c449096152ca24b51262d
ACR-0540f33130d94b019fc491b3da13725e
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PluginsStorageTests {

  @TempDir
  Path storageRoot;
  PluginsStorage underTest;

  @BeforeEach
  void setUp() {
    underTest = new PluginsStorage(storageRoot);
  }

  @Test
  void should_consider_storage_invalid_if_file_doesnt_exist() {
    assertFalse(underTest.isValid());
  }

}
