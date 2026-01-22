/*
ACR-be4c2baa5e624ba7994f5acc50308204
ACR-2bf17ed93dd2457487673b464b81f311
ACR-42d85dc966124d36a52c9871c76e1162
ACR-a367e242fe744619a71d10ae543a1c24
ACR-2f3491e651be43168ac78635e04ee45e
ACR-80943db26f3a4f1d9a6cad4c43728308
ACR-8f793340d51c4bc08617565f6062e2c4
ACR-68fb57f85e874bf19b92ef1155f89b93
ACR-87a857bacb2d453d8cbd90591e98907b
ACR-efc5e67e6d4e4dd19576cfca31f74d0c
ACR-522ecbda41214b74b305a1290ca5d64e
ACR-b05e843508b745ab9753fa66b00685c5
ACR-18c481152a254b698947016a6400c2cb
ACR-4dd6bfa563ac4c9198a284677cfc26af
ACR-52c6ccddcf5a440bbac07cc43f94f134
ACR-103cde0978db42628ed2a65d0df7b750
ACR-b84963d4ccd34797b067adfb5c37c830
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.CheckForNull;

public interface IssueLocation extends WithTextRange {

  @CheckForNull
  String getMessage();

  /*ACR-74ca5b7637b14ec1be5280d8b00bbb49
ACR-32f845fd8e5445b08569a5a5e0843912
   */
  @CheckForNull
  ClientInputFile getInputFile();
}
