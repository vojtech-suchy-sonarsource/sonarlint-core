/*
ACR-d23329a48f2440a29122ffa4b9aed41d
ACR-14d9c6f24fdc4634b48593c0b0a324fc
ACR-2cdf81e48c864f4f802616b238cbee23
ACR-73cde60fe77d420fbeb1b0e974113e9f
ACR-948cb5c857f14403a9b5415fccf41491
ACR-bf6963e912f34be58f479a69e7b387c4
ACR-257a0012c98146c0b91c59a6c95e4dae
ACR-f9aced5b6f3a4730b339a94a427c164e
ACR-c0eb2ac7203845e5966ed728e3cf2e36
ACR-2a53879543e74f75b5ffd59e770a6582
ACR-efdde44f802046959e30a3b8123e348d
ACR-6b8d2907c5044aae8ee02a8a916a3dbc
ACR-4d342485dcad4bb5993aee1c50e228f1
ACR-5736b38af849432189452fce6a0c8751
ACR-bcb9c644c79249bcb482d9a0790a46c5
ACR-576da6ab0c74484694e059c80cc872c5
ACR-e43ee66beaa34a6fa9951d8b579744bf
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;

public class PathTypeAdapter extends TypeAdapter<Path> {

  @Override
  public void write(JsonWriter out, @Nullable Path value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public Path read(JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return Path.of(in.nextString());
  }
}
