/*
ACR-6ebe0c8899d24475b8224db193498536
ACR-6416b498443c479a9283951fd972e10b
ACR-f38fdd3980f14fa09f1c822c843dece7
ACR-45df090fa144449083042c34af0f65fc
ACR-08bf5a8a58d3448fa3a2cc5e312990b8
ACR-f6c165a35a5f400b9c1b70c34ee6bfb1
ACR-a71cd17c1bca4acc9822f15415b4a9ca
ACR-1a839db4a4a14944969fcd7ecd947d4c
ACR-7e88b4920e134b55a07d0c61bf5418af
ACR-b7265c35ae41459bb6b292e2b65bf2ec
ACR-99cc34f2cf2b48cbb286f09382f6704d
ACR-59a0a094bc4c4791a0f72cce4e812f56
ACR-add9e9b7bc3f4038ae1d63852b4b8e06
ACR-c0a0b80bb4fd410b9b1e338281f6df7a
ACR-214ea93970de4719b1b666be81d95985
ACR-7d4cc7b2953d4e35ad621347352cc4f6
ACR-b04a6a93e21e4d08a619c7a0ef6e4783
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
    /*ACR-0eda71a88b8749d18ba3f7ae7667b1a6
ACR-8d7c5c72535b4cafb48152dcf1fef542
     */
    T get() throws IOException;
  }
}
