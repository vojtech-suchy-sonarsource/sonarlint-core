/*
ACR-79691cd77faa4b28a127c2345260f413
ACR-dab49d2c6c3c4d1687bc98d34082814d
ACR-7a7386d3987c4b24830145f226ffdda8
ACR-277dae994db042dca3cd90fc19bbb3f1
ACR-2ea71bc114934600b868297107166915
ACR-0ac7e69470b74af29741e95cc9f11791
ACR-b7b8bc555cc94dac91a70bb234cd0cf0
ACR-a3513c12ed524266ba2d22587866ba08
ACR-8ebd3f1985794e5b993d81ba7292220b
ACR-f9a4bd49fd274d18a1e8b0b3eb019898
ACR-57e112a008114de78b6ab9dc8ba98e44
ACR-f658574981a34acaac18fc5264f9bf58
ACR-cf48391a485e4afb842512fdea465238
ACR-9cb463a6cb8445588ab85825da794dfd
ACR-5f6dc34a935241fc9634a66ebbc98264
ACR-65590860fa5b45b3906817fd6197a506
ACR-61224b7f87be48788bbfa2bb03de9f8b
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
