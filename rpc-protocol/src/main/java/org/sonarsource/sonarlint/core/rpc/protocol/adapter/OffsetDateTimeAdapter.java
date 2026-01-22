/*
ACR-681c379af7d144bd9ddc4b86e2a2e87f
ACR-6ca18dfc1a1b4b9ca55e38da97f71dc7
ACR-b26bfddd39554c55ad25979d65696bfd
ACR-343861dda4db4711856b108c57044dce
ACR-45f94784f2d443479a044352ce15e84c
ACR-bbc3fd04eb93485c8841987d26db181d
ACR-aa4f3b05861d403e98e23ce5255bf85f
ACR-d329b51c13c74c94a5edabd8de4d9b13
ACR-91ad52fdad254e8397a2d0f52ad985c5
ACR-448f96947a8641a5824fd8022dbf6958
ACR-3d6a39d372734e8f91035c34be184e28
ACR-f0213f5c44154a4bbd67ec8e30e31eab
ACR-b3f3b7077ddf48e897146a9fa5b8137a
ACR-74660bfaf8a842f59a9d5115212bea26
ACR-5171384cf6e3444ca7844df4332fe2bc
ACR-d1c16d0a234b4230abfa84aff5b61dd6
ACR-63073b86669d492081b268da21947db0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import javax.annotation.Nullable;

public class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {

  @Override
  public void write(JsonWriter out, @Nullable OffsetDateTime value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public OffsetDateTime read(JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return OffsetDateTime.parse(in.nextString());
  }
}
