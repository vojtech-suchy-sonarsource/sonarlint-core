/*
ACR-4dc956a7ef0148efad2142b5568b18b4
ACR-fb6e3be1b663489a99d3d7cce6e1a9e8
ACR-0582babefe274bbb9c8a12e9278b5835
ACR-087f6599e7cf48feb02c939e9566e359
ACR-ae244c8766194645801bc295b47653f3
ACR-8b99d0102e004c58a3c9f9b94f036190
ACR-db746885416d4866b3c43d91a4fd7cd7
ACR-f1ef2927df0d4de985b561c45385ba8b
ACR-39c189f1c2344b6888a9da11d00b5620
ACR-054cfe04f5c7455c8e151866eb6aeb2a
ACR-1a74078a586f4697b75ed1045f2b0a46
ACR-5731af1f1f724b2d807c4f2e9f45f563
ACR-46a9b712f67c4ca7a70c7af8733db0e0
ACR-75f300f59ae544678885ac37fe173fc5
ACR-04a8a088e8864ad5afe42984c555cc55
ACR-332ba73354754cb4898e73a397ce56bb
ACR-597f04f4aadb4d66a457918134c75005
 */
package org.sonarsource.sonarlint.core.commons.progress;

import javax.annotation.Nullable;

public class NoOpProgressMonitor implements ProgressMonitor {
  @Override
  public void notifyProgress(@Nullable String message, @Nullable Integer percentage) {
    //ACR-cbc59b2e3f4a4665a65ca2d208772ab6
  }

  @Override
  public boolean isCanceled() {
    //ACR-4e4365d7d6bf47aeac4733350e8d90bf
    return false;
  }

  @Override
  public void complete() {
    //ACR-566591f8bb7844f788f444e312b48569
  }

  @Override
  public void cancel() {
    //ACR-c51446ea78a54741917253d0e9c062fb
  }
}
