/*
ACR-926cfd0ac62940cc8f9cfbe771eb932f
ACR-afecb0a4486c40d5a630e2912969e85a
ACR-713ca9a13aa14eb8b6e05d25e40ed5f7
ACR-e9e59336df5642d5a36506e18a6fd1c2
ACR-719af4067a4b4aff84f1bf02bbcc870a
ACR-ff6d60343186430aaebc0b1c74d12846
ACR-e973b06f9ec94642897a1d726c811524
ACR-8d454a4198fc48aa8a7a24723936740f
ACR-5e72f2e939324a3fbd402b6c02005597
ACR-d3cb1670e15e447184c2aaf7963cd7c1
ACR-38aa25f9908045d9a9152a6b45d9ebdc
ACR-8a87016fe6ff4ca39cca8ca8f97fc53a
ACR-fbfc3b4a8e3a4bc69fe72d4ad2ff7aba
ACR-5ca9afb6b5ed402a842b28c70dedcd8c
ACR-2dcda4d05a5047c6bdd020aa11582f60
ACR-76dd07aa470546838bb5c9da18dc8531
ACR-1519a2884ff947f3ba4ac3dbeeac7539
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.dogfooding;

public class IsDogfoodingEnvironmentResponse {
  private final boolean isDogfoodingEnvironment;

  public IsDogfoodingEnvironmentResponse(boolean isDogfoodingEnvironment) {
    this.isDogfoodingEnvironment = isDogfoodingEnvironment;
  }

  public boolean isDogfoodingEnvironment() {
    return isDogfoodingEnvironment;
  }
}
