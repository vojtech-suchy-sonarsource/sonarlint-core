/*
ACR-4b981b970d024e0d8e1e6ef02d7060c1
ACR-d52f4784502e4469bef5d32172b19526
ACR-425c4bb1406b4d41afda4d9d188d04bd
ACR-7197cc7b5b444fa88368d395c5f99d10
ACR-94426b8707544b4b9be982801d906cb9
ACR-7e544f3e9bb74345879f316219fc9259
ACR-265cc92abfce42a6b39d6b1be59229e6
ACR-2d11aa8400434589aeb99e24790da92c
ACR-92523c606a134a2dbe9422ef123930e5
ACR-5b3dac6d1761499ca41db507dc08cd62
ACR-75245190eb78407ebfcba7a2cbd7da66
ACR-119aadb28cda4d20a53357f333b92b0d
ACR-58c7700150024801b45439268779c022
ACR-122476b5b8be4f03b5472f277d4460f9
ACR-2494db4b436341b79d879e917f94541c
ACR-9c1a119e24f04a0b870e25665e88eff1
ACR-2425fa3fd2d44a458507dbcfe6b2764e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.net.URI;
import javax.annotation.Nullable;

public class UriTypeAdapter extends TypeAdapter<URI> {
  @Override
  public void write(JsonWriter out, @Nullable URI value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public URI read(JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return URI.create(in.nextString());
  }
}
