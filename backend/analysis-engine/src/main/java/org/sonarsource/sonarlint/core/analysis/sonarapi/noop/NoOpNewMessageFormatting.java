/*
ACR-5e355782edd440278f9e4acc1310ed9c
ACR-1e0a96d5032942c19d672faa8f63646d
ACR-e0946011d6ed4b9d840a155452cb860e
ACR-3160821fc654444693d5fa1b29964489
ACR-2fcd69caf96b469d94dbe1de077e3576
ACR-8ba6374314e34796a4e2c3df7d2f2ee0
ACR-77f91afaf94b449590c16a6074db92e2
ACR-b648dd853cd543b398da1f4533791b35
ACR-b8a1d5cef19f4ab2a51f61d89582b620
ACR-bf6cf2bcea4746eeb53048696cdb7e2e
ACR-924dd2e091ae44e2a102f0bfd90dd7b8
ACR-5a30c22b33cf42f4ac248d8fa88df534
ACR-7b3c05a96f7841dd990ce01760f9c254
ACR-188e820b81274e6782773395603b095e
ACR-8221c62ab2ce4fdb89601a158a2a42d0
ACR-87d13cc96e2940dd9e6f9fc5f305c48c
ACR-a2036ff659bc40cba35d761b3cb248e3
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.sensor.issue.MessageFormatting;
import org.sonar.api.batch.sensor.issue.NewMessageFormatting;

public class NoOpNewMessageFormatting implements NewMessageFormatting {

  @Override
  public NoOpNewMessageFormatting start(int start) {
    return this;
  }

  @Override
  public NoOpNewMessageFormatting end(int end) {
    return this;
  }

  @Override
  public NoOpNewMessageFormatting type(MessageFormatting.Type type) {
    return this;
  }
}
