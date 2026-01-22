/*
ACR-6abc30e51d3b4ff49c5a2af8ad6bd711
ACR-5eb339c76a92436988a7824728885f69
ACR-61ef43d874064ea1af3509ea028c4978
ACR-58c5ee76c3104008abd43c0bc00afbc6
ACR-ca9684f8d9a74d18906c3c28571debe9
ACR-e1ccf0c91ce34cb683b780f6a7232f71
ACR-e212ee0e4b2442b2b086bffdddf215be
ACR-2c6cb8ad5c2940e0be475e8ce6b6f084
ACR-2a9afa72f66e4eedbe44bdfc9c385a0a
ACR-b59df492d14044fc9670e654fb5dc499
ACR-f9b5e764d6e04eb182250c4f6039f665
ACR-8c6aae095c6f4fc28c5ea2ce087447dd
ACR-ca6c11692d5848aea202bb2facc87c18
ACR-e3d44a07d1284982b68502853cb3ff87
ACR-86f295c645fe4873b151cc6d03727ecf
ACR-ca46e7f9b34c4da28ebf7a306ff3d515
ACR-045a5fb2494d42f88ebb2b7daf243551
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UuidTypeAdapter extends TypeAdapter<UUID> {

  @Override
  public void write(@Nonnull JsonWriter out, @Nullable UUID value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public UUID read(@Nonnull JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return UUID.fromString(in.nextString());
  }
}
