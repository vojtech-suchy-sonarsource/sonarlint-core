/*
ACR-4a4cb4c8e7ec44f59c24dd68db87dcdc
ACR-4c9cc0fa66e5402395206a5ac0ba4c06
ACR-c94477d14596476085a0c789858f6078
ACR-a8d8cd18b0934093b3899a678806f03a
ACR-2674aa0709664976bd9cca64616c29a6
ACR-f072766a94244f3ab663e75d1113b2c8
ACR-1ed3e88d16244c8fafe85a49fce31341
ACR-4b338c4ad08848658a2180cb6dd8482b
ACR-6f087eaa98324b73a3d445a06a6bfa1a
ACR-07b74384273145fcb20cc1e25b070868
ACR-8b58caac44e14583abdc8d9f5e36545d
ACR-51b9820312b7456ca5c5f621c8515373
ACR-afa3541b68594e42b16034e1f144eac3
ACR-dd0ec45e254b48f096f293c7f3aa156b
ACR-6f7b84f9fbe24a02bfcaeade68e951a0
ACR-471452bd51cd4fdeabb8bcc8883d2dce
ACR-16b5620b968b4a9e8d6e596b6bc63361
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
