/*
ACR-19ea19f128ba4186a93f93d8e3090885
ACR-85822d13191d48ddb001821de4fc680d
ACR-3a3115d182fc4092b65cabf476da2c29
ACR-707efaf09e6848ca9015799d40e57f93
ACR-8170660107154018897575900f4aff53
ACR-8d574f5554834b44b4101aaac5828ba6
ACR-9cca5a31e221415a988f9659064d870f
ACR-56f2e03cfe5948b99d23a6a90b69ae32
ACR-ae60099a7f924df8a34f0e2628b1f80d
ACR-4ab0557128e3436eb3a71104f0eaf07a
ACR-86716172787547c8a997da5d834b4af1
ACR-f10d2fb77641449f99ce842642cd9ebb
ACR-1dbe413bef544e2d9f051651be8a77e4
ACR-c25dfe97191f4685a2822c7feb67d0f3
ACR-1976fbcbbf8749a8a0696e6f1a8b0b20
ACR-1257f076a0ce42a7b24c3d240c12e68d
ACR-eae947fb4d0f4200b0101bc808affc92
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ReopenAllIssuesForFileResponse {

  private final boolean success;

  public ReopenAllIssuesForFileResponse(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
