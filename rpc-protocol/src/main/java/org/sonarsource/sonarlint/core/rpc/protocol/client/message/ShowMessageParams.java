/*
ACR-9257763f40a847d9ae2060908c999e1d
ACR-fae254ee860e45d392938c0b973bd6eb
ACR-d6785bb2a3a04a0e81d24e2d8bd69e06
ACR-b6315b9bcba34402864f9cc333748030
ACR-bd75c7cfbd7e4e43a17b17d5754c5d80
ACR-41c1f989e1fa43959a7d98a106ea1fcc
ACR-94621d8f319d4abd8a7c2c6fb48e23bc
ACR-6916198ffbcc48e2bb3e4ffe3c3ddf36
ACR-2228512768c4478abe00032bee8a3a7c
ACR-60a2f188395d406b8a1a0a1701162f99
ACR-62a7642392ed47c7aa7984831cb5cdad
ACR-30fbfe0344e94a4bbc3ebc54b462917e
ACR-846c70e955d544948e9fa426fa1a1e22
ACR-d64f94d24bc746adaf824a8d6ed9bd59
ACR-db129e186caf4067a131a59d739d6f26
ACR-95195fa848e349cc83acc56057cf35e3
ACR-1f2c69013fd2402c85d7465fe4b532b2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-421ef087f9834ebc91c8aa4080402963
ACR-65a2bfca3f244d0fbe77887b973ba23f
ACR-105c0b81f9f24361ab318ec45afc95a2
ACR-f88974c60869452fb6d2d971a4f73c93
ACR-005a627a51ab41b098776caffe1bf69b
ACR-347bf8b2e5d44f158a6380daf8f13dad
ACR-d857a50351514e0ba88f33a6917c12c5
 */
public class ShowMessageParams {
  private final MessageType type;
  private final String text;

  public ShowMessageParams(MessageType type, String text) {
    this.type = type;
    this.text = text;
  }

  public MessageType getType() {
    return type;
  }

  public String getText() {
    return text;
  }
}
