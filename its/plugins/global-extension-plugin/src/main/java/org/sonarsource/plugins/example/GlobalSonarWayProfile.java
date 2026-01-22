/*
ACR-b3fab55af5484826afb7cd7c9aa54183
ACR-11c4506be9454ea195bf2ab0ab63b046
ACR-b3b58b9da2474362b9940faa34f757fc
ACR-cb9adbb130564875885328e6e9f20ce8
ACR-d32483c3d1e04f92a940d61a970b7034
ACR-933b32691cf748f8bd663570a029da2b
ACR-7e31200e461642a4bb56ed604ffa5d73
ACR-2b1ad64df0ee4c518fb096305c19bf3f
ACR-d91481740b154e40889213288c8d3db3
ACR-e16b1fcc26c34242be3c0fb818e2c701
ACR-072736b34bcf4dc7a184809a940d80c8
ACR-0548b262ecc54562a536f59cbfca2a33
ACR-94b6b854a4da43ffaa71c4d961fa13e0
ACR-71baaf5b55174effbd6d562eebf36210
ACR-07812f9211574afd833524606f55bda4
ACR-32a669f94bab4032a7fcda89b661ba60
ACR-1be92c5b693842d580b23653cdcd8f03
 */
package org.sonarsource.plugins.example;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public final class GlobalSonarWayProfile implements BuiltInQualityProfilesDefinition {

  @Override
  public void define(Context context) {
    context.createBuiltInQualityProfile("Sonar Way", GlobalLanguage.LANGUAGE_KEY)
      .setDefault(true)
      .done();
  }

}
