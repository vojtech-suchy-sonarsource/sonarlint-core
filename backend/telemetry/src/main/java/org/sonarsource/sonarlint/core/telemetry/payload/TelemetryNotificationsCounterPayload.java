/*
ACR-31d783715251422389f7f2c34a1609ac
ACR-a036f4f2a91e4a4282a7eb2621c03f0b
ACR-8bdcb56fd1ba41a49f3277ffda22c203
ACR-4f9acf82997f420a8c76448bfd8f1e12
ACR-f864c6fdb00f410194cbec156e26ca22
ACR-a68ee476fdf04bc188195c020ebb1a35
ACR-8970f3de5fa540a5857f2a97f3184e6d
ACR-4ea394ca6353430491b3370ba9b4099f
ACR-02a519801a1f458aae0a62f51b8df905
ACR-4acbd7d21ada4f409c1fdffb1cc56fd9
ACR-f163aa1eeb9840c1ae7b3511da189144
ACR-96616ffee0694c0aaba020bbec30269b
ACR-6efdc26524fc43afb6601a238df9f154
ACR-d77a54d749904c538766a4bf44f9d0ec
ACR-3090d156e4e840959aa04e520fdc35b5
ACR-178976ccd28f43eaa2e9da303b7923be
ACR-3ab91eb45739494c9d76517a718c47fb
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record TelemetryNotificationsCounterPayload(@SerializedName("received") int devNotificationsCount,
                                                   @SerializedName("clicked") int devNotificationsClicked) {
}
