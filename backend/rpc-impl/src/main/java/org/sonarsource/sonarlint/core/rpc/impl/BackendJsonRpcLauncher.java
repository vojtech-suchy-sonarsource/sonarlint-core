/*
ACR-9e493d1acaae470a93e96a23912c2f25
ACR-b5478422fe5d468ca8bbe4a219aaa61d
ACR-055287ddd2ee4615957c0fb5e0b48220
ACR-7f403100bf7d41dcad307023aa548c21
ACR-2aa067d602d34a9880a9a182e65ce1f9
ACR-5a9f4694c79c4397897accbfb7941c58
ACR-d645879aec0f40288473e969897a3922
ACR-9301e299463e4a83b8b7ace93c1bc93a
ACR-7130b59ece8040d4aa26704f7527ebad
ACR-fe99a74b283e47f7abde954e8997fb66
ACR-261d6b77c20b4aa592a8280e4cf84211
ACR-396c1ea8f832443e8f04f74b7a99b1ce
ACR-92daa79330904ee19a4d6c02cdb8d62e
ACR-856904e81f0c4bcdb75ca6ce9ca6e4e4
ACR-f19eaa67537f4d3cbe6f34b1a774276e
ACR-75462d689d1741e0a5fab9a67a3a8f0f
ACR-87b8994e10aa40738b3154b2394246c6
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public class BackendJsonRpcLauncher implements Closeable {

  private final SonarLintRpcServerImpl server;

  public BackendJsonRpcLauncher(InputStream in, OutputStream out) {
    server = new SonarLintRpcServerImpl(in, out);
  }

  public SonarLintRpcServerImpl getServer() {
    return server;
  }

  /*ACR-3d7f34d7047b4fa5ba68c213b093dfd1
ACR-eaa27d84a75c4b0eb8e88eedc2dae841
ACR-3d066337284e4e98bfd6b68fcb5a2719
   */
  @Override
  @Deprecated(since = "10.4", forRemoval = true)
  public void close() {
    //ACR-f16821e1f3df4910a5cbf857c1c16e4e
  }
}
