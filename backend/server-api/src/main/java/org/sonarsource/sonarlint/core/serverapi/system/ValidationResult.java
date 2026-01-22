/*
ACR-f82f71eb0dc54019bd097077e0abe00d
ACR-29304b0155bc4e8c9d470dcb6bbd1a8d
ACR-f6089ab7363d43ad889e0eaec995f03e
ACR-639e6fc1c75246d7ab3b0adcf832501a
ACR-023789068ab54354a95c3c490b9b8dbc
ACR-f1da6453538544acbd5a2f5f3d3fd326
ACR-a75f2e99ae7940f3b3bb2e0f28868a29
ACR-16eae9db5929415e9020a96779833b41
ACR-3d1e94e2ccfb4f619ea13952c129d753
ACR-98d82766858d48d29f70752c200210d8
ACR-0ec88c4f1e1242b9b7accedcaed91683
ACR-49a09b4599034e32b7a097b375d4e4f1
ACR-c15e87deed6e4afe943f58c074a60718
ACR-252a1c6584444f2b97f4a78e0fdc7c44
ACR-b13f289d0b8f492c8ebb082b6beeff44
ACR-682871096ee94d2b84f33a05f3f01667
ACR-e9300fe5ccc2476c935c0fff89965396
 */
package org.sonarsource.sonarlint.core.serverapi.system;

public class ValidationResult {

  private final boolean success;
  private final String message;

  public ValidationResult(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public boolean success() {
    return success;
  }

  public String message() {
    return message;
  }

}
