/*
ACR-6a4b015ec73547ecbf9abc202ef73e34
ACR-ef288bb761de4431a183359b81f66566
ACR-3661d485aa2e4804a6fab7da70210bfd
ACR-bd6413329135482e99f256d36ac381bf
ACR-6ef47a3cd9394cf38bfa37715790b6f1
ACR-274971d7ac70456cbd0df538b6717671
ACR-61959aa4b9e94ebeb15be15acbc2a084
ACR-826e98813b4443a4a6cabbafb0366490
ACR-883fbd358a7245a3b3d5c3f7664c121b
ACR-90330f6daa3a4864a195fcbd1a8bd17b
ACR-f6d082a52b1f4e5784d1cd61db4604d5
ACR-7d7479734e8e48308dd8a9ed022412d4
ACR-6b299d8cc27a47b5a116a1e3c52a3d92
ACR-620a3c4247f6439b83ae77a478018ab0
ACR-3d8ed36a8cae4596b47c1d8301414679
ACR-ed96fe6edfbf43fd90a30baee67bdbb7
ACR-9dae6458f53445e1b3708e072ed9878e
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public record TelemetryNotificationsPayload(boolean disabled,
                                            @SerializedName("count_by_type") Map<String, TelemetryNotificationsCounterPayload> counters) {
}
