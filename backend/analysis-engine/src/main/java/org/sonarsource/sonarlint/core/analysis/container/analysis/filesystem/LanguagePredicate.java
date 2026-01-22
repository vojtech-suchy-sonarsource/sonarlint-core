/*
ACR-a46801df951245ca8892ea8f27ec5ec8
ACR-047c0ae920214b7b8097648446fead5f
ACR-5f1b2d127678422eac2df2d1ad654832
ACR-c8deb9e7b81242d98815cd13725cf2a0
ACR-72825b67dd794b23a5c16b342fd43970
ACR-60ec20463126441ba9e7d809bbecb1a9
ACR-14842a73925e4059885b435ae1b146fd
ACR-54bc073bd4f1427482f02d1e4433418b
ACR-3ff21f91463d4712994fd50f396510cc
ACR-93cb234f070d4ff5b40366217ae435d5
ACR-10c1066f75f0483bae56f22f9103ab59
ACR-9c6f584ca78e47afa0a0f99b296c17f4
ACR-a151b1ad872b400480af6b00a235e444
ACR-ec19f4a86c074d4eb49fc13e5faf17fc
ACR-98f62db9c469462f8bae48717b012761
ACR-649966e425104ea1830ecc901fc19f04
ACR-baad1229b80346beaa4250497da0b574
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;

/*ACR-153b81b7c39a404c81f509cd62bedd3d
ACR-1cdb92c2f5e447cb8c374b4b0284b23c
 */
class LanguagePredicate extends AbstractFilePredicate {
  private final String language;

  LanguagePredicate(String language) {
    this.language = language;
  }

  @Override
  public boolean apply(InputFile f) {
    return language.equals(f.language());
  }
}
