/*
ACR-07e52c855bff4c03848329b0a6283574
ACR-1c7e4720802f47fca08996c5fe43ca86
ACR-450fca0adca44de0b3dd003906d7a2b8
ACR-7d8a542b0f354c638c3a7080dccda69a
ACR-ab9918012e91436fbb646bf8ba201cf9
ACR-2df4f42d90d9480485b2866943c44d19
ACR-9f9336a20a924420bb567e7fe5f5fd30
ACR-f20d974a155b4786a89f4db45f7de169
ACR-b0be404f23784c1498184cc9dd530413
ACR-9a3622e4fe1a4fe9a1512819f729ccdf
ACR-e5defd5b72484446896a407514743372
ACR-29f6ab8d84e2487f88a9bd73bdd819bf
ACR-503a0c734ac54f8d8cf4d72ca29938ad
ACR-da65aca4209c49589667d6b8beb3fba3
ACR-3e39ce6b237b4f70b09d4db068055a66
ACR-cc414fcd70eb4653a39040f56535d9fe
ACR-adb13a36fb8a4c5a97d96773bae1e3f1
 */
package org.sonarsource.sonarlint.core.commons;

public class LineWithHash {

  private final int number;
  private final String hash;

  public LineWithHash(int number, String hash) {
    this.number = number;
    this.hash = hash;
  }

  public int getNumber() {
    return number;
  }

  public String getHash() {
    return hash;
  }
}
