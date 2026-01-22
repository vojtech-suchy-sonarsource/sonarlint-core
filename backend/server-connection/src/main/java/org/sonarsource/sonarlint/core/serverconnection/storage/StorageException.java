/*
ACR-b1cb68e6b20d44268518b5cc6ad9ae50
ACR-109736cdbdfb4dcebde992c0d5f2e9f1
ACR-6e07972162a044e3aa80feddda1f5dd5
ACR-23a8ba19d6f54d0bbd5a102fc4f36c28
ACR-5d2cea1e071d482ead486be4f1dda7b1
ACR-b4ce29cde1d54f07b1d075b2068e567e
ACR-02da2a338ea84508b9e488b040c4c012
ACR-4ad38de0ebb94ec692ad353e3066b2f7
ACR-b0440492c427408997c0b7ba5a15f92b
ACR-0bc0ae22182f4baf819f987785a93647
ACR-24ac2d794ac84f5782ff3a842359ec4d
ACR-573503d690a044cb9c75419b3b6e8e5b
ACR-d9520e7c8ad34922842c1c59ceb1ecce
ACR-677b156c24a140d5b6f14441096ae9f7
ACR-7c80a9da19394d779fd176c8a2d1bd6a
ACR-77a9eb10eb6942b1a7dc8d1e524da1c7
ACR-897480f3205b4c4cb5161e9cb7602161
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import org.sonarsource.sonarlint.core.commons.SonarLintException;

public class StorageException extends SonarLintException {

  public StorageException(String msg) {
    super(msg);
  }

  public StorageException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
