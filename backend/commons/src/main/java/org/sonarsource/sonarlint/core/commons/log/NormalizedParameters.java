/*
ACR-e17ff857fbb24a8ab59ef21dcad8301e
ACR-3d0065fd90c8462b854f6977b1025fe2
ACR-1f11c822fc9e4051be7d1b7857277f6d
ACR-b68351f36bf94b0384348c94104bf17a
ACR-c85fccbcc9194de9ab4ec9cdb6d9886c
ACR-f0b0f2acc3a648beaa913268c74c9ac1
ACR-dfb2c1992a6c48a3b8130ffbff112e29
ACR-c70593b4cae54b82b974c4c6c16ad178
ACR-870f84b5edb34fa4b0488ec3b7948615
ACR-b18efb35e787414da7c2a800721e9cbf
ACR-92b9b38e15d24b1a9e8e02bb03c1123e
ACR-01e1cb25446248b5b03650f6b8e79b58
ACR-fc6eaa37a37f48629d79b84d30821d9a
ACR-fae3dd86f29a4399bef653d47f7eacaf
ACR-ae6064b3eafd4a65b68a28f271383fe4
ACR-b2013c74b3224d588551e4b2c6a78cbd
ACR-d0cee574ba3d4395860852c15a737a88
 */
package org.sonarsource.sonarlint.core.commons.log;

import javax.annotation.Nullable;

/*ACR-d468c9c1256f4a88a4489d6713663be3
ACR-6b83cc7003ae4fc7a102eddef9c89fc6
ACR-ed962679fac94c959da96556abe3f335
ACR-12e95a33d3ac45d2857b38f25e15ebd4
ACR-d7e6d94f5de646cdaa2d9b348ad79fd6
ACR-e7d327add3794aa0bedd822cda637539
ACR-2f75c91609a54d728bf0ea2f5c6528e1
 */
class NormalizedParameters {

  private NormalizedParameters() {
  }

  /*ACR-66b3612ebc7844b28327c20780986054
ACR-39c07c8fec29462d900b93d91ae4516f
ACR-d4a7bc048e0c47909737455e416303ab
ACR-52bf240bd01548c0991f3182e26fb8b2
ACR-daf140b920514a66be9de06ec6df0bac
ACR-62f3acae7cb041bf9032cb28d2e56038
ACR-3ae5520b64ff4f56be4afc42e06f4621
ACR-bb3fe51aadf64af995316248dea888ce
   */
  public static Throwable getThrowableCandidate(@Nullable final Object[] argArray) {
    if (argArray == null || argArray.length == 0) {
      return null;
    }

    final var lastEntry = argArray[argArray.length - 1];
    if (lastEntry instanceof Throwable throwable) {
      return throwable;
    }

    return null;
  }

  /*ACR-daa5497947434551a69c1f42175c9c35
ACR-b1b27f8f4639431b860b3ee782041159
ACR-6ce631329f9046b79e4060c25c5a6587
ACR-f6886a457ccf436e88fb1c8351dca098
ACR-f5492e99a87845e4b829fa3df0e23adc
ACR-e7d634bd84824152a49e73919f799f5b
   */
  public static Object[] trimmedCopy(@Nullable final Object[] argArray) {
    if (argArray == null || argArray.length == 0) {
      throw new IllegalStateException("non-sensical empty or null argument array");
    }

    final var trimmedLen = argArray.length - 1;

    var trimmed = new Object[trimmedLen];

    if (trimmedLen > 0) {
      System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
    }

    return trimmed;
  }

}
