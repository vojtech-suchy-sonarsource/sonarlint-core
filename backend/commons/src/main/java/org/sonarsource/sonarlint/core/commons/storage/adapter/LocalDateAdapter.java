/*
ACR-869c3879ec3a49739806cce257c0a937
ACR-c59de6493e114fb2be264904e37c275b
ACR-1df40a1636a643549101045328ffbb87
ACR-a0c4759703e84c979fbc9bca94342cf1
ACR-899ec17bd8a240329aeac97d5b985839
ACR-b82cca71059e48a9822d4cee967336d7
ACR-a38b398559ce4ca59cc0463b2a16eed6
ACR-a6b898395127467d8a24bd1e99c05ca9
ACR-1718445f9c5a4758b07f8a42899549d9
ACR-78e7fa3dade84f9c89fcd0ddd7d7c16e
ACR-cc5651c826b04049a5324dc259e9f6d2
ACR-227d9a3c7dbd46a48170901905ca1a89
ACR-e1e06ebdf4944e67bee7eba77c914e43
ACR-4b6e82630ec74b00a72c01b4589270e1
ACR-61c9ff221ede4778ae49d4e3e4598980
ACR-96c98117a7604f5f97f02bd2855d255b
ACR-79e031b11b5943ba99f6809b1b1e691e
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
