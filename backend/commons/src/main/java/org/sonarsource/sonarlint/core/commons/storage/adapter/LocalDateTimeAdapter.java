/*
ACR-e435e320048b4b82a962e3b0d875c01d
ACR-ddf8c1b0b20f4e7c8b0edd3d9721e4d7
ACR-ac7b1b25bfd0494ba020549387cc4686
ACR-839bab3c9a344fb3b7fe73ca8395c66c
ACR-413ca502cefe4abcbf53ce0cced8233a
ACR-a2cc8e72d4ee4a01aa3c4b6b971df01b
ACR-ffd0ffec86a149e99cb86a395d6370fc
ACR-997e077c2ff545e09cea3381f0757c11
ACR-1d22065b2cd04ceaa0b1482a2ac3ede0
ACR-a00c317754fb4fd18169708f0706d72e
ACR-be085296fabc4fd09fa1022e90c4bed9
ACR-0382921827654b9c90b8f08f3130e241
ACR-4b126b73622d4ec5979087dcf31c0578
ACR-07ab781566b542f796eaac683cb250c5
ACR-0d57cefce4e44178a0c5e96da9ce8bd2
ACR-4bb85a227b9c4e1fa10351a01ba843d2
ACR-e8c108d4bce540a387ae9826696b1a23
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
