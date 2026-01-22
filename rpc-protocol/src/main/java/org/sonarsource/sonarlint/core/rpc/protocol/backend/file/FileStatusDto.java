/*
ACR-144731f7b3b84794bf2a313862a99127
ACR-87323b86154e44db85548596041c9a09
ACR-2e4579ad0f2142a48719ca16f1a9ae93
ACR-44794a39c6684cd2a3f9d618f010b7f2
ACR-6022504d63cf46a6891cba19ca26f047
ACR-672c0ae018ed46db995774a9b12de94b
ACR-7974af73b8e5418c976c8c8867b90fa3
ACR-f8a62e5643f44c799dc73efe6d8e88b1
ACR-6e05b9a134df4a4ca74909b0c5de4e6e
ACR-bb86519c6a154221ae424a89ddfc46ba
ACR-5d0e34cf928e44818da55ee522e0ea29
ACR-c9bdfc97c53b426dbe6783bbf9a1ad31
ACR-c6a664360efd40cfbe56a8705f592c86
ACR-3f7c324a82f646d4bfedf1d160d00a45
ACR-6fae0987a7a442f08e25e4c405639094
ACR-502adea1aaee4a3597ee7d2888a19635
ACR-af47e7638d5a40fba3bdb094cf1f9705
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

public class FileStatusDto {
  
  private final boolean excluded;

  public FileStatusDto(boolean excluded) {
    this.excluded = excluded;
  }

  public boolean isExcluded() {
    return excluded;
  }
}
