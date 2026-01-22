/*
ACR-c6d2eca4c9ae45bc986d65e29b0e8256
ACR-86b4ca1ebe1f4ef6b70a8abfd7075125
ACR-ce32cb2a745245b680645aaa7d94e5c6
ACR-ea5746624eaa4281accc93061cf7694e
ACR-7499f5bb129047218156352c416f1c82
ACR-5d2d7e7389584e8b88960dd83090fbb1
ACR-b24b1167194d4266a75b789616462368
ACR-5ed33eeb5b8d42ecb1e0e7ae3dda631f
ACR-e028201913f54122a31cfe6b82598cee
ACR-1423df22691541e98bf6c27c895e1fd0
ACR-6ff13c48a0fb4a43a30912116b57611e
ACR-d795eafc6f2e420eb0a259f09d910b73
ACR-be0b24e0480345ae94b1694a0deba5da
ACR-78064ff0ab604d62ad3c80a144ca5cc0
ACR-221e0115a1144ba5a6caf1d581895f48
ACR-6ed6337538a1449a85773e7975de6750
ACR-55a9880b07664666a0fc0f7431748c2f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope;


public class DidRemoveConfigurationScopeParams {
  private final String removedId;

  public DidRemoveConfigurationScopeParams(String removedId) {
    this.removedId = removedId;
  }

  public String getRemovedId() {
    return removedId;
  }
}
