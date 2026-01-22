/*
ACR-b25538072ba347b6b5cc5cb653d1b2c8
ACR-af91981f7caf4cceacde2fccfc9262ec
ACR-07b7b0e2be7f40aeb37ac135377ea198
ACR-6f0f0bdfc12c408caebe47152a5d9452
ACR-9e00dcacb74048b0b31c4d02bfee2f9f
ACR-c0e4756b1783435181be68c22c9a4c07
ACR-3403d455441543e0b34be6b38855d3ff
ACR-29b7aabd64774817ad5a3c1fa3c4635c
ACR-49b9795ebd3649479cfd5ed84adae43a
ACR-10fff7e78e874cdcb07953d1ce2e034b
ACR-73e57cb4ddde476fbdefa06fd74085dd
ACR-cb036338ce1d45ac94d23588368ad01e
ACR-3e120cd0ccf449dcbaf3e070f4c311fa
ACR-16161f5635514122bd84dbf9a93483c0
ACR-b028ee2577294661bca41a3f493b5f06
ACR-0792b039a19b40df9832a99a44b7135b
ACR-0ac88e770bf04e5ca72631389c47d61b
 */
package org.sonarsource.sonarlint.core.http;

import javax.annotation.Nullable;

public interface HttpConnectionListener {
  /*ACR-5eb6c9d3e60b4a68bff0b0197c81bf3f
ACR-690c520ee7084a21be6a477dc6d327e3
   */
  void onConnected();

  /*ACR-1463aa0cfe2442868182a949e0889c3e
ACR-b35a27b23a1240f2be5c0815527189d5
ACR-dbac851b508a47508baf5009f77868e2
   */
  void onError(@Nullable Integer responseCode);

  /*ACR-4378c7b2182444c79da81513dac33fd6
ACR-bb8ece784306403db63ae78d0febdec9
   */
  void onClosed();
}
