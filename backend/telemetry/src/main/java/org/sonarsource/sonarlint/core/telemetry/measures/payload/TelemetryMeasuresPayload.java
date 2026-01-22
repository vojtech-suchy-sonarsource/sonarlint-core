/*
ACR-980f9d0ce5a542768d4b8e62ba5052b5
ACR-0db4b2aec9844eb8aaf6e01e2590fe18
ACR-b9771ab1b3764b8eae646bf41a78643b
ACR-255564265cdb4081bc52d6b3b50e8842
ACR-5f689f33c66246768f71981de93043c4
ACR-8ed62323f4c1494d8e60f9df1421db1c
ACR-4a3192eef932462ea2f874ea9057cf59
ACR-12e2e7c41b3848c59c7083ed4ffae305
ACR-06064b38c626484696ee2a4da92b2f83
ACR-874cc590fae846a298d16c76b7d2178c
ACR-350b47817bd6470d8934d826895e7a0d
ACR-f5052d384a5f41259bf206880e3129d2
ACR-f9066de93c3f4b0f9f442d155761318a
ACR-862a98a8c7fc41f487eb0852cf7c2913
ACR-df28c227d09b42f1a113a9d50d9c3ab7
ACR-a2ffbc3528784d65b778f93d0bea38cd
ACR-7f97e864d39747f4a31dd51680320689
 */
package org.sonarsource.sonarlint.core.telemetry.measures.payload;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.time.OffsetDateTime;
import java.util.List;
import org.sonarsource.sonarlint.core.commons.storage.adapter.OffsetDateTimeAdapter;

public record TelemetryMeasuresPayload(@SerializedName("message_uuid") String messageUuid,
                                       @SerializedName("os") String os,
                                       @SerializedName("install_time") OffsetDateTime installTime,
                                       @SerializedName("sonarlint_product") String product,
                                       @SerializedName("dimension") TelemetryMeasuresDimension dimension,
                                       @SerializedName("metric_values") List<TelemetryMeasuresValue> values) {

  public String toJson() {
    var gson = new GsonBuilder()
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
      .serializeNulls()
      .create();
    var jsonPayload = gson.toJsonTree(this).getAsJsonObject();
    return gson.toJson(jsonPayload);
  }

  public boolean hasMetrics() {
    return !values.isEmpty();
  }

}
