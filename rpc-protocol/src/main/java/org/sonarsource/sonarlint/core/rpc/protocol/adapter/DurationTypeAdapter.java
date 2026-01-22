/*
ACR-3679f55fc86043b7b960a19224818be3
ACR-4e20970d7c974411a623859bad56a27a
ACR-a8e6e8a4a41b44cfb0cb22f77db8b1f0
ACR-ce5d1dc6100748ca8c33ce963ecd0810
ACR-b370798d1e604bb896577e65c3eabeff
ACR-be4a75be42634f16820b908d2de50353
ACR-c6e8855ce2ed420395a11561bb8bd073
ACR-d86a64188c6449f3b135c065bf3799e8
ACR-cf3e38a8b518453b945ed3011faa06b8
ACR-360eefe2e4ce4ac88c02d24cb3e829aa
ACR-b7111631a5244bae8bb545921b7242ab
ACR-3535e1d418d349ddaa34cbe1534f6d09
ACR-6bb6c1b73f4a444fb9b741008557c497
ACR-b9dac9da324a43dbb094202f669d174c
ACR-0db3e20b842947049acd2bfe2947e42b
ACR-1dcd151ba6734ea4b440f49291c75ee7
ACR-46c29f7c395a4feabe47c2fb74005358
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
