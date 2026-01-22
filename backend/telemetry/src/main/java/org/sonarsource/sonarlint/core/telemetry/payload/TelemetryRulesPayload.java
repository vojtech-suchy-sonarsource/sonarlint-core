/*
ACR-372a6e86f712402997dc8884a7e3ac08
ACR-b3f43a7a7888407a993220ae60a02d01
ACR-f033891dc9154c44bb746ae940fab779
ACR-8025dc076b784d49a321722d2855176e
ACR-363cb137f2124cfcaff26b9db6869d5f
ACR-a5a0b845c2c84fb9bf6ad55c7b557a8e
ACR-8a65b5e62ab347a295387dd5fe18cd9a
ACR-633c26787527471d9522c279d7035f1a
ACR-28a0b9306ea147e9af5672e5f5a730a3
ACR-050836a3d3af42649142571ea63c1ff8
ACR-9acfd23615d541349613e69a8d13b639
ACR-86606fd1b0c64c66b31018fab06529f5
ACR-a2e5735a7d024b709e1295e186f0b755
ACR-00b969ff73e44fe085e93a8027344c4d
ACR-6cbacbf00d8a42369fcd541222a03e61
ACR-852029de78a34bbfabd0cb8bbe1e9f4a
ACR-5d2b2847c3ad492984e6dd0b90f34ee2
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;

public record TelemetryRulesPayload(@SerializedName("non_default_enabled") Collection<String> nonDefaultEnabled,
                                    @SerializedName("default_disabled") Collection<String> defaultDisabled,
                                    @SerializedName("raised_issues") Collection<String> raisedIssues,
                                    @SerializedName("quick_fix_applied") Collection<String> quickFixesApplied) {
}
