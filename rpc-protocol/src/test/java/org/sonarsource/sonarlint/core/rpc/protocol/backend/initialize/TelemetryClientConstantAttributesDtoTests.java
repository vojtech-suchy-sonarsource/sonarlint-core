/*
ACR-cebac727d9624554a56d33640809c40e
ACR-b68821f5d8cd47ad96d3e17aad86e3c9
ACR-97c08e6b797d42658ef76b77a9f31ce1
ACR-f22ec0459afd44db9ff94f122052fd9a
ACR-99d7d54b47d54a5aa65be7b442d1b86f
ACR-91df4e1202254281b9241f8ef7293aac
ACR-b8f28e2a70af4ac38d814572b6cf347d
ACR-f063ec4cc04e4c36a74398ec4b082871
ACR-309de2ced8b44ea79cda9f201052b3fb
ACR-3b523dab82154a5caf261b1c29cc39ce
ACR-f613645cac654ce58079540378c94bbf
ACR-513ef029d4d141e995134f1c56572996
ACR-898b3ab4ae63470c90ffcb608fb1d661
ACR-43cfa7c65d2c4fefad2a6d6a8fd891bf
ACR-c0869cee2e5e48538fa6aa4548d1ecf0
ACR-35b50983c5864f868f0c0aaa1b6469c4
ACR-243228eb4af7484983287408429b7a03
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TelemetryClientConstantAttributesDtoTests {

  @Test
  void should_replace_null_collections_by_empty() {
    var params = new TelemetryClientConstantAttributesDto(null, null, null, null, null);
    assertNotNull(params.getAdditionalAttributes());
  }

}
