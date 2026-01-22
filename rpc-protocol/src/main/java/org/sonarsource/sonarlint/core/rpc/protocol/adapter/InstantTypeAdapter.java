/*
ACR-72b73dc488074da6912f5db99833d547
ACR-5c224215dc684134afe7b19476bdb73b
ACR-7c4229f1b0f94f27b5c3ef855b2a5a5b
ACR-a296b81074b54479a15162709a9e87c8
ACR-80aa192bd1b74e609dd31d5e0548ad60
ACR-f2de53b654434922818c4a20d47b9dde
ACR-ee8f18e0cdd84b44b4784eab4b72f025
ACR-293573d821ba402a923d8d1f537361aa
ACR-bf87ea3438304625b5fac1684989db16
ACR-8ca56c80a6f24f7b91228e08bca157b6
ACR-13fe39dada854c30b1c9797da562e1b3
ACR-1efb873d696a4648814af0085638206b
ACR-1ffce6720823430988abfa657523b265
ACR-55275cca0d96430585da9013c216e405
ACR-009be5d0897a41a79519dc476d526009
ACR-c94f506016414fdb8c22a430956bc49f
ACR-56c5dd43e6c3427e84f03dc4c7a74bbd
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstantTypeAdapter extends TypeAdapter<Instant> {

  @Override
  public void write(@Nonnull JsonWriter out, @Nullable Instant value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toEpochMilli());
    }
  }

  @Override
  public Instant read(@Nonnull JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return Instant.ofEpochMilli(in.nextLong());
  }
}
