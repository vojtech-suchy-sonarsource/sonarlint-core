/*
ACR-da720fcd8d3c4fd08cb5b0618d4e2d84
ACR-4da68af5a4ab4386a62c423e04215fdd
ACR-b6a993d43ba74365b54dbf8b57fcc2bb
ACR-b62a903f9a2a4e8fae3d555ea0bc926b
ACR-f307e83190e8470eb78fc976f462f0e2
ACR-acd2357251de4c3a9c0d7a129fdb33bc
ACR-237ef86d7ed14a3cb64c440561de0303
ACR-294ea0c1a408435b927c6acc9e521e9b
ACR-eebb4780f30c4f6c8ef47939d3b6437b
ACR-7466b8efc4b24e94821e93c4d5b8a723
ACR-7e91a313eedd462dae818140b521524c
ACR-75e3622254eb4f018320e391de22827c
ACR-91344dcf4e7b4700a60aa70fa02d164a
ACR-4cde0d8d3ee64903a21851204d63b0c8
ACR-84d96248a0f546f8a5b5156d3d914046
ACR-96706e45fae84c3bb55e7c55621eb944
ACR-a75ed05bc7d94ac4b1a67b44bbc9354a
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
