/*
ACR-f72fd023980a4d6b971c0647260e4fff
ACR-f3159b6e3dab48f494ed12a3b1354fb3
ACR-a3553a5bffeb458a85e79e085995e3a4
ACR-4d3311cee67644d19ba03e42aede48dd
ACR-29a8cf5d23a44649be270f357f299d2d
ACR-3c42c31083b649458a698521ec9f63a9
ACR-70129ecaaeb94c54b2562263de02474b
ACR-052f12fdeb8a4e488fae80c84ba1b819
ACR-694a40f5daa44906a8fe44d3e8b0afa0
ACR-a42b5d224e5b4e85a67d205ace1d479b
ACR-409ca5c87aa14611873ba3d145a73261
ACR-fe085ce9363049feab418a4aec69b72b
ACR-c0a2fd4108b142a4b6f0303a95b8c3d9
ACR-1c255abcc9a74546b0c35532221d799d
ACR-ac9764ace35241fda2d3f68f2d5c43e3
ACR-b97954999b3449bc931891752ce72672
ACR-9cdf212b89dd4089987a419b528dcdbc
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
