/*
ACR-e3dfaab39d6d46acb5bfd90d00247079
ACR-e8de1bff6b6549688d672b0211de2158
ACR-3ba9b92267ab43108f23cf00701386da
ACR-3f2882f2afb44c059d0f553c992348d6
ACR-37ec0a2cd782446e8a5c5164829aa5e1
ACR-4a6f9ade8cff43c2826b83e5c41c7d26
ACR-4aa742adaee747b28aab9b1c80a12116
ACR-aa0c2dc7aba743f3bfdff898a6727c8f
ACR-aa9b441f0ca646b6b592febc3cb27b8f
ACR-2e2a358b8a6c49708aa8a24b89bdd071
ACR-6ffc8662c5b14fcfb522529596cc3b54
ACR-93fe46d67b6945d8af9d2251d4f6b2b0
ACR-e358ec7b8070422994f7bf2797654318
ACR-1a5bd250ba4e4ae8a6c720f8a3e2b7b8
ACR-1664177afff94124ba368ff591ce25bb
ACR-71868b8f2b3e4830851f2b14cb6c1e23
ACR-b5c3fb58ae224c8f88e38561fd78f64e
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public record TelemetryNotificationsPayload(boolean disabled,
                                            @SerializedName("count_by_type") Map<String, TelemetryNotificationsCounterPayload> counters) {
}
