/*
ACR-94a489e4f3e947cf87168ba7e71d925d
ACR-2c94cb9fc617486481edb5ce7cc7face
ACR-bbcdb42adaf243c08f373cfd3751bb09
ACR-c18f93268c83465ea8a6101ecf04ad27
ACR-e4fdddca2480495ca004ea177a37f911
ACR-2a5d53ad3dff4b55a045603f405cff12
ACR-0649253f54fd440aa8c630da78662335
ACR-c00f0f2c4441467a9031370522d30cbe
ACR-b114a6cf08e549e79cf9a02b42593be0
ACR-5362d42a211e410492ffd5713ad62607
ACR-5988327b39fa424bbf902fd9bb3ae030
ACR-5cabcd7b65c9454380ae65f2efacd7a3
ACR-1ddcf4add3d349139f3fc30a401cef82
ACR-55161f8d46d74d4f8dfd70a248bb444e
ACR-07139bc7e6f649b69b6cea729160b365
ACR-b4b5b8856a334928a1a8778de2ef7ad8
ACR-ffaff3115b33429fba1673fc7ec3d913
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
