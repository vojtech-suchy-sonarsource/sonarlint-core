/*
ACR-bf4bae9527de4b6b8bad1e4d9a254a8b
ACR-dbd58afe043e41f583f53bc645f2ac64
ACR-061ebd6f71af45ed8cafcd987c5557f6
ACR-5784995576d24e14befa4d78643a6c79
ACR-11c7a14807be47d5839cbd5732acc047
ACR-bbc4dc7553c0438dbc74a1387b3db36e
ACR-a3867ce3701d45c389459eec4a94238c
ACR-dc975b1df4cb4e9097e835dc7f8b16cf
ACR-a7ae3566565444cd93cfa16ba116e8ca
ACR-460e668fdaba4084a0a89fbeab5dcb08
ACR-77ff3cbc1971476a80a261a226831130
ACR-fee881befc7e45d0a60d5134dbf6d7a9
ACR-10f7debdc4744ccbad912ef3ac50a72e
ACR-8aa958110a764f809ca9ee653303a963
ACR-f81f2368172348bd84b9817c2214a80a
ACR-70b355e11bcb4da08b7f9a813e9df6b5
ACR-4bf4c5aef4c84b8d9153e3eb87513ebb
 */
package org.sonarsource.sonarlint.core.backend.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class EndOfStreamAwareInputStream extends InputStream {
  private final InputStream delegate;
  private final CompletableFuture<Void> onExit = new CompletableFuture<>();

  public EndOfStreamAwareInputStream(InputStream delegate) {
    this.delegate = delegate;
  }

  public CompletableFuture<Void> onExit() {
    return onExit;
  }

  @Override
  public int read() throws IOException {
    return exitIfNegative(delegate::read);
  }

  @Override
  public int read(byte[] b) throws IOException {
    return exitIfNegative(() -> delegate.read(b));
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return exitIfNegative(() -> delegate.read(b, off, len));
  }

  private int exitIfNegative(SupplierWithIOException<Integer> call) throws IOException {
    int result = call.get();

    if (result < 0) {
      onExit.complete(null);
    }

    return result;
  }

  @FunctionalInterface
  private interface SupplierWithIOException<T> {
    /*ACR-007c4c69ccdb4772ac6a5a22b95edcde
ACR-69e6f6bce2554633a74927836a3114f3
     */
    T get() throws IOException;
  }
}
