/*
ACR-f594a24cc35545fab5daf9ce3560472c
ACR-f1d8bd397d0f44e197a4dc06c4b9b62f
ACR-5f964948b4f04062842d19d53a404559
ACR-38c159b71df44ecd8efd7c7733214be1
ACR-15d516f1f1554c83a8ad81c912471bf1
ACR-d2ad726b26a5450686191af542db7595
ACR-23cfe68e4d0942bcaf6c5c974d62a98d
ACR-e969da8b9f6c4bdaa53dbba5ec9f6976
ACR-dbad8b41f72e4a9cbe1e6a2db4211052
ACR-f6cf95b16d704ebd9d86685419ee667f
ACR-b9207a0c054e4b7f8e6933e2235686ff
ACR-e88d3ea190b94bc487b57d8f05219b64
ACR-91cdf500ff3741d8afa9449b4b937103
ACR-2109804ee9364e9ead806596e4ae1689
ACR-7f3f9b79b9ac4030881a97dadf82dbde
ACR-ad4cf6184f3d402fa66ed3b1ac27a49d
ACR-afeed387881045d5945d1804925a4a44
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

/*ACR-b7ab0dbe09724b35a0b6e480dbdfcef4
ACR-ba334c7ffde449728be350361792a627
ACR-c2ef144beeb2410083bf834c29560208
 */
public interface ClientInputFile {

  /*ACR-d06c7a19380e42fcb5e35d092c9a652e
ACR-38e03f66b1fd442ab67294a528efefb2
ACR-89e16dbf187d46669c4322f4ea0830cc
ACR-dc1777fd393d4e1dbe7d4831e05a32ed
   */
  @Deprecated
  String getPath();

  /*ACR-addc1ee89c8c44feb810ae70901557c5
ACR-5b67bbbbcf3d476c832911e9cfb4178b
   */
  boolean isTest();

  /*ACR-a9f75aba193a4422a01fa27819bba19f
ACR-117b996079d04fbbb029e672d6dd059e
   */
  @CheckForNull
  Charset getCharset();

  /*ACR-2c9e029105814cf6a96f24939e437b83
ACR-f7b2193dfb7045278af17db0a3e2f299
ACR-b2a351e91f474f0083ee84a987761cf7
   */
  @CheckForNull
  default SonarLanguage language() {
    return null;
  }

  /*ACR-a0cbd302e4be4b3c806aa574bb152a9d
ACR-1681434d6fe64efda2b4eff17b82f932
   */
  <G> G getClientObject();

  /*ACR-da389c3de18d455a93ff5c783e47df52
ACR-1de1f6d659324dc7b7094f9d7737e231
   */
  InputStream inputStream() throws IOException;

  /*ACR-4df074a272534351b15ef218321f5d8e
ACR-43be16f0a62849f7929caacbaa3fd976
   */
  String contents() throws IOException;

  /*ACR-1f75a40059c2445183475e43b5885b8f
ACR-ba63a4e4ed074265a6a0e2d617d738eb
ACR-92a194ee21354ea28d1f79b6d5dbf63f
   */
  String relativePath();

  /*ACR-d60d98c2a7be465db6784bb354a2ef34
ACR-2fc2d7868c8c4271b15ac888a00f765b
   */
  URI uri();

  default boolean isDirty() {
    return false;
  }
}
