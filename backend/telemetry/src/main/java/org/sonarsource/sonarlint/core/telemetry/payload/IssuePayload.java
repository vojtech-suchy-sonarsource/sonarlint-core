/*
ACR-b6512f9f11ba49fab79f170d9d5dca9d
ACR-c8e125d1434e452580b9034d17f6d1ae
ACR-0deeaec7828c4fcbb3eaea3cef5fab14
ACR-58748c243e9049a0942da2e58f724fa5
ACR-b2c142976597421b9df1b4dd14ff8d1a
ACR-ed3eb9d8cd22484995e4c9d75dc85ac9
ACR-9b3c68d3cb744f649fdd3fd8c02a3c8d
ACR-652fcc687a56410d91559cea371b8462
ACR-8766c9f406da445f82cbb8b84b76f34d
ACR-5bb2546fe1eb4a32aabb778b970446fd
ACR-6203de557a88438aaba6ae654a4ea164
ACR-2b7bba7fd50d4a1facedd04349b1668f
ACR-b2013f83981b4319831efa587909e30a
ACR-0a525a30efce40a0b048a746d81ece16
ACR-680535d245bd4cc990b2c82356871b34
ACR-90a75b422741438db6256bf746391f72
ACR-b40dd061ea274a9db983fa10de27386a
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public record IssuePayload(@SerializedName("status_changed_rule_keys") Set<String> statusChangedRuleKeys,
                           @SerializedName("status_changed_count") int statusChangedCount) {
}
