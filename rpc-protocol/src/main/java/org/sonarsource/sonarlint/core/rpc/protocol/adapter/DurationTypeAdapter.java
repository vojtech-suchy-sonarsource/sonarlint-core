/*
ACR-b6e14de38e9245638fb5e7f61e120598
ACR-feb7bce45ee3446ab12d5e478989f437
ACR-93ade1f580164e94822a0e6196902bd6
ACR-3d0bb58a2ff342748004133ad90284b9
ACR-c261f65047ad472a819288c1780075d3
ACR-9f2d26af1b5b42988d04d07210da3655
ACR-afce2fb389e84fd9829180af063863b1
ACR-c555752053654ae788dc134679961bbe
ACR-1853cd7fba2542d28aa8ba76ab9c4b29
ACR-995b9a5c5f664197aa4065f3064f864d
ACR-a9a9497e4d1c432f87c7ad166f15c977
ACR-e1b2d65d07b0461ab24aa54412d4480d
ACR-1462f0c20a324253bea2b2d6e81e9336
ACR-6c5eeb16f8fe41bbbd302fb604d5963b
ACR-93c64df0917949c7b7fe13b4f2c4382c
ACR-8833fc1f7154445ca4f10cfca6607676
ACR-17becec8b6794d30835df25a21ee031f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;
import javax.annotation.Nullable;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
  @Override
  public void write(JsonWriter out, @Nullable Duration value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());
    }
  }

  @Override
  public Duration read(JsonReader in) throws IOException {
    var peek = in.peek();
    if (peek == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return Duration.parse(in.nextString());
  }
}
