/*
ACR-88996c35a2cf4c92b7f02aa89d33fbbf
ACR-82474222eca84a7195aa8aeba328cc1b
ACR-126b3729746c4d2cae9a765114e51c24
ACR-11582547dfe6444f9baa9f043a40e03a
ACR-77ca5ec925be46f283dbb6afb8b6ca3e
ACR-ac47fe2ebfab4706b136a6e7ab431623
ACR-1d0aff66d46e40b98bcff32a7846d02f
ACR-697231e0d16b48b9a351cc2d1592b299
ACR-5c0e92579e1d45fc97946a6700536baf
ACR-78bca2aa632f4913b8cbb6ff4030f78b
ACR-ec3ada73a1994d34a9ea284a41024137
ACR-a5377664c2d54b8e953a71e2eab37e93
ACR-3eba373ae0bb440fbecd58071b0bd6de
ACR-d43f268928234849865787be129ac5ab
ACR-f110cf4337c34d8aa3d107ba8ad18eed
ACR-37846ea1513d47a1abb63dc8f580abac
ACR-d0319bdc23824db69eff86c42b1be3c4
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record HotspotPayload(@SerializedName("open_in_browser_count") int openInBrowserCount,
                             @SerializedName("status_changed_count") int statusChangedCount) {
}
