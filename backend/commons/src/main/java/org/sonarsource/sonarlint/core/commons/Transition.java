/*
ACR-eae5d77a30f341b09d209957d8bd87ce
ACR-177f7072814e48bfb1e214fa12c9d842
ACR-b3a5dd3be1834e728306b2754f551ff0
ACR-60555f1b055f4284ac7dbe550be30874
ACR-2cd37466dac0436eb3c89bd36f62ad78
ACR-484c7c95cda84db787a3ebd24458aa86
ACR-a8a9cf3ec6d94edba3e98b8180e0cd91
ACR-c4298a5fc75a4ea398a049657e56373a
ACR-a942c2e8ac47428bb67723354b0f9915
ACR-aeb5112922c5432e9db267bb119f47dc
ACR-a2ee17f63c2e461883362ebed9a8073b
ACR-9ac64de58b134a7fa5f20e12a0aa7e3e
ACR-14acbf560fc94899a30acc44476310bf
ACR-25f0a5a7e1e34852ae94e7481e77bcbb
ACR-07f1bd9b7e624633b35b153cd1382e6d
ACR-4132eba9aef24635b709c2ba8f4a953f
ACR-cb6eb4bb3962428bac04d0139425a549
 */
package org.sonarsource.sonarlint.core.commons;

public enum Transition {

  ACCEPT("accept"),
  WONT_FIX("wontfix"),
  FALSE_POSITIVE("falsepositive"),
  REOPEN("reopen");

  private final String status;

  Transition(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
