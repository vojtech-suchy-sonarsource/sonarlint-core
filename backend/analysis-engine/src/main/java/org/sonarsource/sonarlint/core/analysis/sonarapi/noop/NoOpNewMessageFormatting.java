/*
ACR-444de3a1a4e44152b059e276f83e9beb
ACR-5a7b197f113f40deb30d5e680a269023
ACR-6a1b58998b97438f96d2bb8af29e2c85
ACR-b3b04e1faaf1425289f5d2aeaf986f64
ACR-09c3ff40bffb4dc3b199734a30e34dae
ACR-3282dea4ae974a378da597dca0ca460b
ACR-853e5f279f62459ba7df0baf6dcddf76
ACR-d5de0d5d109a482c97c58a295f677a14
ACR-aab2c0dfa5e742d88e9c86eb7bd2af5d
ACR-98652b05c5544a2b93d67b4b67a1a0d8
ACR-b8638d47b25b459bba58e7048d2fc4ee
ACR-091c426f77b14131aa567574f907eab0
ACR-1dfab89fc83245fca0b6eb1a98c0bc59
ACR-e381e96856664de3a330ab0031e90d31
ACR-6fa26e8b7dca45d9bb90a07640538590
ACR-e9656c0630704b0e8ec2890572839fab
ACR-155a479c45c24c38852bd5616daecd5e
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
