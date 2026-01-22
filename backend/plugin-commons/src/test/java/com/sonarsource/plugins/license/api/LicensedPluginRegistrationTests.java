/*
ACR-9618e4b0033a47668f67d3e2424f5fe2
ACR-298089158d064cd68d0d2b9775bfe6d0
ACR-aa3ac9c429984ef69351647ad95ee115
ACR-9a14824bc8714fa0bb3b4511932ed2de
ACR-176e145d68804fb58120d5f3a428b805
ACR-6aaa68b2f8ce426194bc3bb9c71ccfa0
ACR-8144661d18444e16a234ad831342721f
ACR-9b43fc4cad26410f84c7221ebd7eef36
ACR-dcc20490434d4564a6e69f683fd1780b
ACR-2b9d210189f54533bf8e14f04906e323
ACR-797ad925e2b54353b042d2f4849647c3
ACR-02934d0571a248a88af8f11dab96ab96
ACR-22447d657a844efaa1a575e3f354b278
ACR-2ab40b0a1fbb4c13a37987fb46f2add9
ACR-1f9d416a08544a4a8938bab9c3991e6e
ACR-d9597bae80264e1a93badecdae4dab86
ACR-b6b0b816b69644e1bbac84cd8474a48d
 */
package com.sonarsource.plugins.license.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicensedPluginRegistrationTests {

  @Test
  void testBuilder() {
    var underTest = LicensedPluginRegistration.forPlugin("xoo");
    assertThat(underTest.getPluginKey()).isEqualTo("xoo");
  }

}
