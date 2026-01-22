/*
ACR-4a51382a193f4877bf4cf20780bd8358
ACR-16490f462795434688e8e2b389d1f0ce
ACR-11b51986150f46659f26cd22f10b4ec0
ACR-c31aa6dacd804745953f91e2639b868d
ACR-0a55089068b84b1d8e9b0970066ee9f1
ACR-9782e2acb78f454da285cb9f304e7add
ACR-5e36e5982f7949398c9712f3e135dcce
ACR-6116d1cf6006495aa86d2ed720258209
ACR-1ef695dd5ca341df91bfd2929ff2227a
ACR-5dafd392a67d4263825f7f3bc0b81b21
ACR-8f9bfeed84284e059c7db288a65815cc
ACR-5816fd716e124fd69e6209bf24ca8a32
ACR-32ac037733fa40fbb244e42a38070a54
ACR-f039f74f21a7440cb25866c2ef65d401
ACR-e28364aede9345afb4984a8425ae9e18
ACR-7f14022845d9452bbaeae164622e8f1f
ACR-cb12352912d04e6fad39a56a422d6e8d
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

  /*ACR-0b6acf521a754dc5a71f8d19396ec875
ACR-356d32e6c4324844aa7d89c7d1d830ba
   */
  public static List<Class<? extends JavaCheck>> getJavaChecks() {
    return Collections.unmodifiableList(List.of(
      AvoidAnnotationRule.class));
  }

  /*ACR-10a3c8d848144168a317a806ef66b8d9
ACR-1a03a41920a24b159fcf8cad59d675fb
   */
  public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
    return Collections.unmodifiableList(List.of());
  }
}
