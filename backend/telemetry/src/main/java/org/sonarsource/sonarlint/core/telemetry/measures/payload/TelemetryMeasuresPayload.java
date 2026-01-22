/*
ACR-cb8ff6adff06431a99ff0c6cf1dd5799
ACR-99619a8c03084aec86d045bd50c8e93c
ACR-72ae59249a35479a844f1573dfe12975
ACR-5ce983c8ddf04bdb9c576bd0600c9008
ACR-9718dd90814d4e608e8cfad530aaf216
ACR-694c8ab21f4c42c69a77419eca2180cf
ACR-af7bf9cc534240959a5dbac3e53b1d44
ACR-416d44d95af44e1fbc6e459e11dc0a26
ACR-e7db2715af864a4f912ee969733e1e3c
ACR-1bc3f80130d847baa1bf6f006ce44ae8
ACR-f7b6fe58b2f04641b5e62268d9ce49bc
ACR-bf5ef52eefed4b38a81ef3035c20a6ad
ACR-88d5406f5ed447f287dded22d931228b
ACR-b76adf8622304e2c9c4e0f7ca80c292c
ACR-356f7daa189245ea8bf91e7d48c051a2
ACR-a3a405bbb4f84f578208b69c8fb97042
ACR-b29a092711a641c78741ba2016688736
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
