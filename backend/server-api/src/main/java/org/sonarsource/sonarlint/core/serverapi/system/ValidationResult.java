/*
ACR-cd449451befb48e18f0739d4351c2ad3
ACR-1c76f347c4364ce6a0274f6e2dd13f7d
ACR-59a13ecb95f040bca2bd023b62ab4b72
ACR-f3a77df08bd74254af0a890801693182
ACR-f9ff4d22078e46958200169d58e683d0
ACR-4a69d8c57d9c428ab77c156787fa8655
ACR-a3f36646d2264681a33482f528f4999a
ACR-1653ccc30e2345f09bd9f1e304a8a698
ACR-34794fb96357454997b597356bbcb2ba
ACR-68bc1ac75fb5445987ae69bbc6e71dda
ACR-de866310596e41baa0dedeb48f1aae41
ACR-5bf39871a908492694cd5d0a739b1354
ACR-998d49034ee34a53b6b89214d6685103
ACR-7dbec5db195b4befa1485d65fd06b4e4
ACR-a5d2bda2b24540319291fd4a7fde8a29
ACR-4bb289e91ed448079a3502f7e74e19e6
ACR-8b5df6a0406f478d96f030c68d06685d
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
