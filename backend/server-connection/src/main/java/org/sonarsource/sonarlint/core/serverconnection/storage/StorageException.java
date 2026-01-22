/*
ACR-7124142672a94af78403bdc8e90661e2
ACR-f2ecff874a7b46b6ad6f855fc5f77fbf
ACR-63211d6bff8440dc81615ef7eff54afd
ACR-39412de16f99421bab684ea063c65b33
ACR-e856c1e7ca744b8ca8da5bdbf069425e
ACR-1bdcb2c848474684a82fe962f302fb58
ACR-274f1b1932ef40509295c2e447a40da0
ACR-6291d879a79c453598006c6bcf93132d
ACR-91b578bda1df46bd9a367ecd402af86d
ACR-36257681ee854b1b8c9708c88c431564
ACR-71b3153e1976482f9d68e8b3a534e541
ACR-ddd8dc76df6e43c09e55839eb1927192
ACR-ef49fa51873f4db3afe2fb123e6fc5c1
ACR-2aa88edeb1cd47cfaca851445e7a3265
ACR-7125f06470a24701854522e1749007bc
ACR-e245fa17edad432e99c71f8c61f3ddea
ACR-867a13d280b64636820a67846c41efd9
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
