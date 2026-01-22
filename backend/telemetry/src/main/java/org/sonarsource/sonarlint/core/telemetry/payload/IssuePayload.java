/*
ACR-eb267128acac4a94ad1a04d2aed57b26
ACR-29b31e0722c64134b1c1aee989c6ce37
ACR-f6c1bc6008b545a4be9933a9631ecd40
ACR-062e6f2767d441a19366ffb31133eedf
ACR-371ac07188ab4ca5a126cd0479fc0bd1
ACR-0e8a52fa325e4da797b8b916d2e85bdb
ACR-5a213e667eab4114a6aa58fc4f02f7ad
ACR-76ee5d530b7349f9b2202d7d2e02c780
ACR-34b31e97afee413fb83a7570027de4ff
ACR-e766766889b2491dbca9ac795a7907e9
ACR-ecfe9e696b914abe9c013d36ddca264f
ACR-0740b6d20c224cd9911c55961002c622
ACR-1461caa8b39c4da6a2fb866fc1c49fc4
ACR-60050404d3a945edb75092738e5dbc7a
ACR-26f3cdbd183946d5b52d4bb43e61071d
ACR-5e4f300e90e740b2a847b385a6ce3717
ACR-476d5fdd360b47f38adc4571721a27a7
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public record IssuePayload(@SerializedName("status_changed_rule_keys") Set<String> statusChangedRuleKeys,
                           @SerializedName("status_changed_count") int statusChangedCount) {
}
