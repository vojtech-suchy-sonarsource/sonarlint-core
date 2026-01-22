/*
ACR-de2a1cb7ed5243b49c4246d595c5d31c
ACR-742f0d22d4f240deb2aa3d9eefc783b0
ACR-f8bebfe9fc8b4cc08ebad959e41ce8c2
ACR-e34caf5816cc41b48806c7583e4647a7
ACR-625f6c000734412a9a792a76a0560831
ACR-7463a1cced9a4f938e7dc488fdb4b104
ACR-00be1f9fa35f40adb288d43333f298d6
ACR-5f5ebef232c84aad9df2530d99169142
ACR-490998d417db4fa0b18528932cfb94e9
ACR-d4b36ccc3b524634b730d8682997e859
ACR-8bd0cab3dc294b0698b7205f71b392fb
ACR-6f6ed9c319c0427a9576cbc5d402e935
ACR-c3393cc20b454121b365a255f79d514f
ACR-8082f19ca47e47d6b7e5b85fba510e55
ACR-bdd9a53821804847abeb5110282ddb89
ACR-bf6dfe3cfe4a482faa4d36694b854b6c
ACR-981efccdf8824aa19232bd92e28fb1ae
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
