/*
ACR-c570f053998b40498d0cb3d5b9513ec8
ACR-973e3b2723a74fd4908f97ef779f4d6b
ACR-195166d068f841cbbed762cf3ec49ede
ACR-4bcecc634d164adab982480a8052c4e0
ACR-7f2bc492e65d4f908664409c9d8d6b71
ACR-804201443dcc4a02bfb5f35cdd0c5850
ACR-c3d8639ef5d64207bc1e7d4aaab6dc59
ACR-998ae9ff5c634b82921552ea17d0f976
ACR-a460e602a6fa46afa0ce4b8c524883a8
ACR-976e6d91f8734898bffb865b8c7691c2
ACR-19b3044d595f41a5b0d12912afe42d55
ACR-a03b2f75295a4046a3eb2021ba8a683c
ACR-850910eb921a4c97afffacce31730b60
ACR-98083e4717d44c2e862dd5be0252c38c
ACR-aae0296a5dcf44b7bf78fa25a3f83f8b
ACR-36032627237c442386f2af2a8532f795
ACR-b31896ffe8fd408d9572523cb9779cc5
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;

import static org.assertj.core.api.Assertions.assertThat;

class StorageExceptionTests {
  @Test
  void withCauseAndMessage() {
    var cause = new IOException("cause");
    var ex = new StorageException("msg", cause);
    assertThat(ex.getCause()).isEqualTo(cause);
    assertThat(ex.getMessage()).isEqualTo("msg");
    assertThat(ex.getStackTrace()).isNotEmpty();
  }

}
