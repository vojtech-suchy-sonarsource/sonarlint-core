/*
ACR-303e9a55dd7e47aba23553457b5973a1
ACR-62b7744a697c4a9580ac4677adfd3bbf
ACR-557d5ea162ca4f5b81d835c6d8314df5
ACR-307734dcf2624d4492ab9b28524adaf4
ACR-4944bd5bd14247e9a9f1090ee7f81fac
ACR-5544210797b5457bb056b80fbcbd30ad
ACR-4130d8f477084f3f946ae64ae64f682c
ACR-1d746271f8eb4649a43dff93a11fe297
ACR-38aca25c1e9e431c9b97ab0e46c8dde9
ACR-e23a3c02eb6b4efaa77588d3fd67d4c6
ACR-b217aa8386214057be53e088ddcaeac1
ACR-b96c0cf824414f4ebab4782a52cf4051
ACR-b7ff5cda775345f6ba659c1c9a51aa12
ACR-bd807376b5a440a9a718a6e24379ff55
ACR-e54e8600c30144f9b5c8095637caa195
ACR-150d47500f3d49d4ba62e6c8a4ffe00c
ACR-4d3035bf61e84e2e8e60f5d284671f7b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class IdeLabsFeedbackLinkClickedParams {

  private final String featureId;

  public IdeLabsFeedbackLinkClickedParams(String featureId) {
    this.featureId = featureId;
  }

  public String getFeatureId() {
    return featureId;
  }
}
