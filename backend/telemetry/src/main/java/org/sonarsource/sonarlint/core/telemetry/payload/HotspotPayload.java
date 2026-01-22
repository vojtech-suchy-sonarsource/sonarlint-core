/*
ACR-e9d7b04d6f2c42619c2b3fe072ec320d
ACR-c83aa4d4573b42d2bd7c863d1861c3a7
ACR-c54a9fb611b44f02be04ad2b521eb853
ACR-d0acea0117514bf0b13f9047d333b848
ACR-e8231313435843999ad3425d7fae8113
ACR-1bb088ed0ed84667842978492d67f930
ACR-690c698512744305bb6cb00eeabc0132
ACR-f890bbbd7d5e4ec79e5347c6d91a882c
ACR-38b7c2d41cd2471c85ac5e5bc48770ca
ACR-3b1fa12210a541f0b420bfa4a0ff77af
ACR-e926c67550a54b0e93a484ac76644780
ACR-1f625356a277435ea4493120e3a26842
ACR-b03a7609a61d44f78a4147fa3b931740
ACR-1903508bbb2c40ccb0b04ffb93e8b3d3
ACR-8cabfd6e5c66434cbacb8e43595429de
ACR-c52550603bfb413cb558750ecc8afaf3
ACR-2cfad554416a4d07b16b785e18a4a459
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record HotspotPayload(@SerializedName("open_in_browser_count") int openInBrowserCount,
                             @SerializedName("status_changed_count") int statusChangedCount) {
}
