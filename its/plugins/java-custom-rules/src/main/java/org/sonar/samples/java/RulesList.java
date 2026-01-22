/*
ACR-bbb7c8168dce4273a01ed59049d6e0e4
ACR-598a0b153e694da8aabd807da780a2f0
ACR-5527ad194c0e450f8a84cee4334cd074
ACR-3efbd645e82f499d918ef8c779a2c2bf
ACR-27fcc8287ec743849b31d2d5619b7c09
ACR-27f1d3c95c4f42c9a8be3b740b43ce15
ACR-14c8785329ab4e698affb08c772d5097
ACR-1cef1921e087426a88d0cba4074b3296
ACR-38d7f2e8182d43579fba55abd4f22747
ACR-cda7c38bf2674763a0943c124a05664e
ACR-7e9b0cc3061b405d97eae90d282269b5
ACR-caa95cc8c0904301a82384131a036e04
ACR-f64c5a22ee4c494496ab05b08ab5a4c3
ACR-36fbe810e27d4532a98bc93944c6e860
ACR-c4a7589ca7a948a3b5e21a5429bd96c7
ACR-e37418d9896d4eee880109b299c54fd4
ACR-ea1081f00f9e443a820693b64ad98f31
 */
package org.sonar.samples.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.samples.java.checks.AvoidAnnotationRule;

public final class RulesList {

  private RulesList() {
  }

  public static List<Class<? extends JavaCheck>> getChecks() {
    List<Class<? extends JavaCheck>> checks = new ArrayList<>();
    checks.addAll(getJavaChecks());
    checks.addAll(getJavaTestChecks());
    return Collections.unmodifiableList(checks);
  }

  /*ACR-0ee55ca39a474d699702ae6816d0f3e5
ACR-6d01961fe5d14636a35d1e08a529bd43
   */
  public static List<Class<? extends JavaCheck>> getJavaChecks() {
    return Collections.unmodifiableList(List.of(
      AvoidAnnotationRule.class));
  }

  /*ACR-e6e286419f2f46db814c9e4774dfa6f1
ACR-2d800dc3018f4baaa088b8fb39432a50
   */
  public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
    return Collections.unmodifiableList(List.of());
  }
}
