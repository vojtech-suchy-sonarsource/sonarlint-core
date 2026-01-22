/*
ACR-390753bd0067400eaa13ce22297ce460
ACR-bd94e4c394254226ac3eff9d9fcd6bdc
ACR-9092fea4906448cba2618ea9be4873cd
ACR-e9255f3215f14bb385532145645cce8d
ACR-a1bb72e02aa94a0faa2f635d888d2a5d
ACR-cd7742e69d11499ab4bbf2cfff5960c6
ACR-d923a3de68ed4235bab4658d5cea5bd2
ACR-9806e355a4bc4e2b86f7e55eac951780
ACR-bb038b465f034587b67245807d1bb582
ACR-802e1eaefa7b49c284627f841d1e6b6c
ACR-30daa0f04cd74491b86fa1cec41aa4aa
ACR-9cc43d8925ab468cb9c72b7b14c4b7d6
ACR-3a295fd1ff1645d8b21c679f6ce74592
ACR-77702f8f9b7d4adbba2abe103696f655
ACR-a9cb8b1c62e64c55bdb38a9a1f93924e
ACR-94ea216e5289455cb0b68991fa7d6f83
ACR-98c9ed4f7e294fcb8c619efe6ebd020b
 */
package org.sonarsource.sonarlint.core.commons.storage.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

  @Override
  public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
    jsonWriter.beginObject()
      .name("date");
    new LocalDateAdapter().nullSafe().write(jsonWriter, localDateTime.toLocalDate());
    jsonWriter.name("time").beginObject()
        .name("hour").value(localDateTime.getHour())
        .name("minute").value(localDateTime.getMinute())
        .name("second").value(localDateTime.getSecond())
        .name("nano").value(localDateTime.getNano())
      .endObject()
    .endObject();
  }

  @Override
  public LocalDateTime read(JsonReader jsonReader) throws IOException {
    LocalDate localDate = null;
    LocalTime localTime = null;
    jsonReader.beginObject();
    while(jsonReader.hasNext()) {
      switch(jsonReader.nextName()) {
        case "date":
          localDate = new LocalDateAdapter().read(jsonReader);
          break;
        case "time":
          localTime = readTime(jsonReader);
          break;
        default:
          break;
      }
    }
    jsonReader.endObject();
    if (localDate == null || localTime == null) {
      throw new IllegalStateException("Unable to parse LocalDateTime");
    }
    return LocalDateTime.of(localDate, localTime);
  }

  private static LocalTime readTime(JsonReader jsonReader) throws IOException {
    var hour = 0;
    var minute = 0;
    var second = 0;
    var nano = 0;
    jsonReader.beginObject();
    while(jsonReader.hasNext()) {
      switch(jsonReader.nextName()) {
        case "hour":
          hour = jsonReader.nextInt();
          break;
        case "minute":
          minute = jsonReader.nextInt();
          break;
        case "second":
          second = jsonReader.nextInt();
          break;
        case "nano":
          nano = jsonReader.nextInt();
          break;
        default:
          break;
      }
    }
    jsonReader.endObject();
    return LocalTime.of(hour, minute, second, nano);
  }
}
