/*
ACR-f09debe4563348b38d8861218951b45a
ACR-22dcee3a98044ad4ae14823e10140e87
ACR-4fe88f0c92274ea8b15ad27bea9dc738
ACR-41c114b7cfb74e9abf608876268b57de
ACR-1fb37feb80f44dbe82e230c0fc710f30
ACR-91f1871a144c444e95478a76aee39f00
ACR-ba5ae7eca0ea4391aa9310bde45ed964
ACR-9465fb1f7c4e4572bcd8bef044a176ad
ACR-ad157a4497874630934d59ced659073e
ACR-76c2e82dc351404fb23452912705d258
ACR-8044bafd49784d9081c33fef7e4d9c2c
ACR-721f97ef7877479f8d81bdb1e744fabc
ACR-e47e603edf2a418f9b0c2050527d35b8
ACR-6a6a8ff1063046f2a20403e6dc372b8e
ACR-b28027dda07a4738ac9f89ec0187e53a
ACR-d1c6a8df0d3e4edc9aadc8671279431c
ACR-81d11b7b618e4bf98cae99331ceb4e32
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

public class AcceptedBindingSuggestionParams {

  private final BindingSuggestionOrigin origin;

  public AcceptedBindingSuggestionParams(BindingSuggestionOrigin origin) {
    this.origin = origin;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
