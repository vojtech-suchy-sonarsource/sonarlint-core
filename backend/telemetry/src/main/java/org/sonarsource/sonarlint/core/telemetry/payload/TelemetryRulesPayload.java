/*
ACR-571bdc31fbe54c1da2311c48e3f00b75
ACR-d18ac95a5dbb45ec8cef1bdbd3162e82
ACR-cce659d8b7254d7a84dbf3983070af1d
ACR-a45d6de8c6384a99883377db93d7070b
ACR-d7d82dda8d2945ae9a597ce748c59bb1
ACR-1277e77948ed49c8bd2a6f209557f08a
ACR-1f686c4647e546f5954097a8b7abc980
ACR-926d10da84f24418a0e0a2b1a8c4b176
ACR-893a292eb2784590a789e65631e17c51
ACR-5334635703fe4aa3b63b7b7410fa6c8e
ACR-6df55384ca1a4f4c9de6dab812bad89e
ACR-ee0a5a96b49740679695fef5d36f9284
ACR-07b746e84e70413c98ddb568b0a8df9b
ACR-df65309fd35846339474cffc3cd205fa
ACR-6dcd66e6488848f29a05039316843a53
ACR-5faa20bd8feb4d2398cbb9b84c933f41
ACR-36dd6bf007d74cc0a575b6edbed676c8
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;

public record TelemetryRulesPayload(@SerializedName("non_default_enabled") Collection<String> nonDefaultEnabled,
                                    @SerializedName("default_disabled") Collection<String> defaultDisabled,
                                    @SerializedName("raised_issues") Collection<String> raisedIssues,
                                    @SerializedName("quick_fix_applied") Collection<String> quickFixesApplied) {
}
