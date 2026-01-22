/*
ACR-30ce5c3b74b241398643af99468a9891
ACR-1a07a4f0cdb34815a84d2a6d6bd305ab
ACR-6ab0e15f5dc34b3f8d303e0f937702f1
ACR-e231ded9501140ab95edf6d59b07fba2
ACR-9e0638a289a04d1fb5eb105eec711d7a
ACR-b5898875f9c442e5b9cb55aae13e80b3
ACR-163a0d6959ff4e62baed71b2e03a7017
ACR-4cf6f8bb669946ef969578585595d92d
ACR-4d2d9ffd3cba453ca54c52f8a51190f4
ACR-75ed202347654b03b417e7705ed3bff9
ACR-d9e5f87df7a24e9294d97dc8023b1bda
ACR-d831705fb9364840926e04c4d1dd9bba
ACR-dec1c0d8a7da47f58dc91dc80f52513f
ACR-851abe0d698647259365873d5d9b9b39
ACR-15f791d8883d457ca77bd640c118c554
ACR-7474e5d4f3ae45b088aa2416ced66dc2
ACR-a3b68e8889cc47cc831af325b3d582f6
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

  /*ACR-69b0aa85c50d49bea259db5b343ba15d
ACR-816de6d2ecba4339b5cb57ab7d4f2855
ACR-97240f2742744b21ab70502fe33a8ad3
   */
  @Override
  @Deprecated(since = "10.4", forRemoval = true)
  public void close() {
    //ACR-62d789ff4e1c4b149e75a075c9941e51
  }
}
