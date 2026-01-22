/*
ACR-21d9a07a18a4479989ff24b0b7d1396c
ACR-7dbb92babfc849d2b1b8693feddadb86
ACR-25f05694c06d49c6b8f8b5a89c1dbc51
ACR-2ae1bcd1b9ed47fa9b88498434dcdc22
ACR-c216df90a67b43c7990140779cbb34c3
ACR-2ff04d9803204a9f858afc753804b2c7
ACR-ee8943adbd314ad89891ddaddb68f022
ACR-34e25529c06042918a461ffff2490154
ACR-fc6d7d20a8ed46419eaebd8d2157a7e0
ACR-3cfed051aa7945849b3925bc678606d5
ACR-3f992e3abb2e4933985aeadf0bee1182
ACR-ee8613e1bb5d4b168b7106bae7fb9e22
ACR-f7ae947718214bb495ce304b6a7f22ce
ACR-ace08bbd97264610941a1d3e409f2ccb
ACR-183f69f1c2484d4b8e4b92e31a6bad53
ACR-f2bf8c68d8394a3f8d57db06eb25cf3d
ACR-358109da7d2e4c3c93adbf960a1546ab
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
