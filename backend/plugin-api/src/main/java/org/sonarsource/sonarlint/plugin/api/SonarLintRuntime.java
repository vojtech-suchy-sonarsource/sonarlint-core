/*
ACR-4a58a10dd2ef4bc2bcf9d92e70fe913c
ACR-588060f926ca48e58c32516d0e58b744
ACR-22f8e44aec9847c6af2d237b1fc80f7f
ACR-4875e16bd4664d20908a3cead40d24b9
ACR-e444533988b840cf8c46e921a8ed79ab
ACR-8a1f2108069e4909aca6a9d5f3f66c4c
ACR-9e2b49eadcb1478b89a45d80507d0b17
ACR-71f7d3b2573248e18d47d3569b8c11ff
ACR-63fdf59648324d27bef9a4d47033d2bb
ACR-361a1be8004a4be6bd6dda9117818f96
ACR-a724d97b14414271821aaf61139c2717
ACR-88791c5dca204cb6abb9e1adb327cf81
ACR-a825011bf3d04842a6dc62f5147cab74
ACR-0c81b1f63cc8479389dd1c8a16ec2875
ACR-fe474458cf54456a8c2e5fe27bc08fcb
ACR-aab4c2f7dd584f5d9a3dae0165d3cf9c
ACR-492dbd3b136b4637ac15f8548ab7b54a
 */
package org.sonarsource.sonarlint.plugin.api;

import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.utils.Version;

/*ACR-54f538c8f78545318135f46bd8b41969
ACR-f5468228ccb5488bb5c7bba7ba34dd1b
ACR-6fcb402d71cc4bb78b96b8d9993e45fc
ACR-aff3091a37bf4bd0a1721cf938a790d5
ACR-0465c3aedc6c4993bf56cfa55e63df2d
 */
public interface SonarLintRuntime extends SonarRuntime {
  /*ACR-5f57157d80b040658daca7ef40feccfc
ACR-d4c4140ac9d849c289c2ee2624eacbc7
ACR-a68b5d8709254e7c8e5e4c4760acc3e6
   */
  Version getSonarLintPluginApiVersion();

  /*ACR-917023867bb74863918f84a2b10cb691
ACR-f30a82514cdb41d3965267f0ae109088
ACR-c02b777d2c8744209c7be1fbfb92620b
   */
  long getClientPid();
}
