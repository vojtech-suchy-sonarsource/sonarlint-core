/*
ACR-a19a8a4973b747cdb52e042991330fd4
ACR-8cd10c6188b5499ea83ebff9eeed3e27
ACR-be0bb3d8ebcf4c8f81efe3c537b09a27
ACR-70d8ae2193e54aad957e7efcd8702325
ACR-ad9ac965609a4fddbea1c51f0ce1d3f1
ACR-7a8c6c4a9c2f4da6832eeaf1c0a0eb87
ACR-48446cd7adb64a0eab3816b2631ff47b
ACR-adde7772fae949fc860536d191abb4a0
ACR-a5e66f023495412c8cf6d36322c9270f
ACR-2e4987d0a47241aba6d6ede572d71a5b
ACR-97108e17755543adba87b9e84056f9d6
ACR-aa0bc1980b044149b22fb77ceb4bf909
ACR-a1ae319f698044d485d9bfc8277bd1ff
ACR-3dd351f315ee40268c951b884a6d9c5f
ACR-8796bd90a2274327937f20e4148591ec
ACR-6b51e40297e44aa08a4360bb8f8e246f
ACR-c553e93fdeb9412ea6a112e0913d5a35
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.TextRange;

public class DefaultLocation implements IssueLocation {
  private final String message;
  private final ClientInputFile inputFile;
  private final org.sonarsource.sonarlint.core.commons.api.TextRange textRange;

  public DefaultLocation(@Nullable ClientInputFile inputFile, @Nullable TextRange textRange, @Nullable String message) {
    this.textRange = textRange != null ? WithTextRange.convert(textRange) : null;
    this.inputFile = inputFile;
    this.message = message;
  }

  @Override
  public ClientInputFile getInputFile() {
    return inputFile;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @CheckForNull
  @Override
  public org.sonarsource.sonarlint.core.commons.api.TextRange getTextRange() {
    return textRange;
  }
}
