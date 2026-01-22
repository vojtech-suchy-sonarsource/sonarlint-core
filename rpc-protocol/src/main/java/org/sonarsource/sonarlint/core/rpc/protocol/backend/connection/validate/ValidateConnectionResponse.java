/*
ACR-1d2e4b81f49c4e588f79a803d12c9fc5
ACR-104c480efb804cc18dd308341d12fa1b
ACR-efdf2aaccdc343438be67b53700c68d3
ACR-857fd3174971488fadc815b1c02352cb
ACR-faaf4567547041549d9a5ca14dce60ce
ACR-8ab389aa13b943aeb0281fea1f8cd53f
ACR-b274e9bba2144e5ba107bb8a8858327e
ACR-85a19f9a99844657b089dd999b7b187c
ACR-5471b53d108444d48c5c17324cb69882
ACR-a46bdb00e1ff46a69c73156bf0852a5e
ACR-f4992b0de69b4857b7bdac17db8ad5bf
ACR-9133793d14424d2890526a6ca1d8682c
ACR-7dd4ac8a50ca45a4b6c2cf02861f9db2
ACR-f01be14684594a8b8dd7f06f4ab9d0ed
ACR-11b55970d2624899b2eea5b697d8cc99
ACR-9890d16eba124fe58b9ef14f1ab6b898
ACR-c92857cfc69b4dcba03b949471e2ac42
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate;

public class ValidateConnectionResponse {

  private final boolean success;

  private final String message;

  public ValidateConnectionResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }
}
