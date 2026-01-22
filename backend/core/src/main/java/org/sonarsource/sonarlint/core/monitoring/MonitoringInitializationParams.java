/*
ACR-0463414eeb644baa96e55708355e5901
ACR-da18dbf1a5d94062a93cf6d6c77cc675
ACR-89d849f750cc49c294c2e4e1f5242a57
ACR-05df8aa68a194b76879bbff7e4c18567
ACR-d60a9c05f93e4a878f1fd9073e2da72a
ACR-998555fc736946498b52325cc09165f3
ACR-5fc01de6665b4704949b6acd8970bbbd
ACR-ab4f9a9ed5fa453fafc11d34b4b713ab
ACR-bd9e0c878f724f2083a4c1e1d27b3e94
ACR-aae7237b57074254910ba4a10dbd2f0f
ACR-9f94ade27704446ea4c2aa2772da34cc
ACR-7a9d80b9b5fc447f994668eb6ee591fd
ACR-ce1f4509f08143e6b9795f36ea983886
ACR-a5f193d227a3482a8ebbb4747011d75b
ACR-9d3f7c5408b843f2834378c7e42bf1a2
ACR-ed6bb0c97ee14a4cb45d987179743e79
ACR-553c8ebcb43e472686e30c292fdc2d6d
 */
package org.sonarsource.sonarlint.core.monitoring;

import java.util.UUID;

public record MonitoringInitializationParams(
  boolean monitoringEnabled,
  boolean flightRecorderEnabled,
  boolean isTelemetryEnabled,
  UUID flightRecorderSessionId,
  String productKey,
  String sonarQubeForIdeVersion,
  String ideVersion
) {}
