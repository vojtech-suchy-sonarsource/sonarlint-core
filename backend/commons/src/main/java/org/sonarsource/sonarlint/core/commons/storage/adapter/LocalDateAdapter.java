/*
ACR-4af06a43acb84a88a8212ad91dec05b5
ACR-a7c0c6c99b8e4a3ca4214daf393ad0a7
ACR-24e7a9feaff6471fa1d258d13a079ff2
ACR-d0f30d6fb7ce470c8067aba0ad600cac
ACR-0d393c923c24493a8beaf242f5bd5440
ACR-06c957e0a7c74bfba86b3ffe02a148ce
ACR-ca4d7929eeaf48b58186fd6998510653
ACR-934d21145c40430fa26f41f9498e92e1
ACR-892fece3ea3149848a771a5eb4bb13db
ACR-e110c698e046441e9aa872ee13cf28e1
ACR-753140090dcf4fa0a901a9f08c7d2816
ACR-5866add4d8864c8e9b3884871d9b53b4
ACR-45974067f2ca4368a16d1642a7fa1e74
ACR-fb557d5f39dd48b99483adef9eb4d0fe
ACR-5fe4424d3dba415ea65f6d93af736d75
ACR-39889b2a4b294fcc87e78454ddcd1ebd
ACR-990287bc57484ef08b9b32020ccc821f
 */
package org.sonarsource.sonarlint.core.commons.storage.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

  @Override
  public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
    jsonWriter.beginObject()
      .name("year").value(localDate.getYear())
      .name("month").value(localDate.getMonthValue())
      .name("day").value(localDate.getDayOfMonth())
      .endObject();
  }

  @Override
  public LocalDate read(JsonReader jsonReader) throws IOException {
    var year = 0;
    var month = 0;
    var day = 0;
    jsonReader.beginObject();
    while(jsonReader.hasNext()) {
      switch(jsonReader.nextName()) {
        case "year":
          year = jsonReader.nextInt();
          break;
        case "month":
          month = jsonReader.nextInt();
          break;
        case "day":
          day = jsonReader.nextInt();
          break;
        default:
          break;
      }
    }
    jsonReader.endObject();
    return LocalDate.of(year, month, day);
  }
}
