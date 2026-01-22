/*
ACR-d7a63588b580489da849d9c8b76fe27a
ACR-6f4a0d53056e4dc69c5eac178e08cf65
ACR-bf8d2f08eddc4c989a0a091956b63a71
ACR-2f35ee8c82bf467ba13eae9b9b2d7bdb
ACR-33d907a8f8b34be6b0f78e9860886be9
ACR-7b16a0fe9b02405ab8f4495b67194cdb
ACR-fba66c6d45c545938ab032e3102cf9cb
ACR-c01fa473a66e48e99e0284c0681a98e3
ACR-5a64a916a86047beb7526c10c0160695
ACR-98d09e5b740d4a66ac0ed4b66ee929d1
ACR-fb046f829eb34e88afba54e41c9dfba2
ACR-0a9e3f0b0b6e4dd68ee8e01efaef179e
ACR-88cdbc1ab6e74ef8b7be40bbf9675cb6
ACR-845d2eff33db4bf1bc6e6dbe2671f1f5
ACR-2f65a35443664c43afe7198bc4065d0f
ACR-bb67ba166dfa49d49f4c795e65c11a50
ACR-e752b69fc6164c6db3df511c13645cd8
 */
package org.sonarsource.sonarlint.core.commons.storage.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

  @Override
  public void write(JsonWriter jsonWriter, OffsetDateTime offsetDateTime) throws IOException {
    jsonWriter.value(FORMATTER.format(offsetDateTime));
  }

  @Override
  public OffsetDateTime read(JsonReader jsonReader) throws IOException {
    return OffsetDateTime.parse(jsonReader.nextString(), FORMATTER);
  }
}
