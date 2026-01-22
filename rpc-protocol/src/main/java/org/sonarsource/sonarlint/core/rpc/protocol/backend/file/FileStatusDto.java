/*
ACR-ad1b2ec1afc848e4b60075f8793b8a5c
ACR-c3ca891bfd4744399edc875d3b5d5037
ACR-7f838c28161d422895d55977b97458b5
ACR-6c53ff7a5fb64880801be99b97487793
ACR-1aad153b861c4b08b247df68f52a73fd
ACR-3e51dac2d07d4a0d9b4c0a0b628b3d4b
ACR-15bdf99c450c41f184e59e736e1eee50
ACR-146da42ee70540e89cc95368fb672449
ACR-142ab3b2ecf4430f92e2dd0d2a893600
ACR-b1051b2f0bdd45bf81ce6793989febc5
ACR-eaf164c17cde45b1913fc726a12e6789
ACR-a55267893c5044aba4186c523fd04481
ACR-2a6a781c64ac42828a299cbdd367585b
ACR-fd8ab2530606484daa54fde5b5e18a14
ACR-64f40b288a3b46e7ba6c002182d2f4a5
ACR-c2e3d5786b2543f1a601c5bf0d7837ce
ACR-06d1e78f9fdf4e84b0a2eb2dfd893e4c
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
