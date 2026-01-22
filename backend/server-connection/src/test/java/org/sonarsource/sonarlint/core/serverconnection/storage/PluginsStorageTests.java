/*
ACR-9eb25665f4a8440c8019252e94288070
ACR-1d616a74096549bca605daff44a76044
ACR-5c349af3c0ea462ba26ccb23d39463fb
ACR-65e752b2ad954f1bb683e3072ef3e8ec
ACR-6e79ed0055f444128cbb04a6466a50a6
ACR-f73a7fd3c2214baba4231641fc2676b3
ACR-464bd2f2a8df467d93c0cbf0f1182527
ACR-db6640f3c0124762b748c9d8d32d12fe
ACR-75eb2689256049f78bd5a4aad3fbcdb4
ACR-57459fc55082415fa5856e3395081775
ACR-500c60674a114d2d8697e1a9b7cc2cc5
ACR-d6a74a42a96b4b768695d62494b29ef8
ACR-92fa5c0602734d848f16c34a2dfd49ef
ACR-62dccc40aec44b7f9ac0a9039037e6ea
ACR-443b7e0ccc484e7b97bb0438b3f9c27d
ACR-603b95c9d5ae4348939ff2c18e2a687a
ACR-ec3b92fc14a54d5798ae00629f379fff
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
