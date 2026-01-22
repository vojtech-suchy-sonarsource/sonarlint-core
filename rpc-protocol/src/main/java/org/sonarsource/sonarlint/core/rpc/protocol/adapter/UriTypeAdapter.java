/*
ACR-2ac87d97affb4698a2787e56dce833ab
ACR-6f1f48d6d0a14ab99102466ce8cffc22
ACR-14abab66a14a41a68fbc720f4033fea7
ACR-b60b6b8fd35d4cb7a9d32baf030b8be5
ACR-e2f70f5b093d4df4a090866fc8b1a698
ACR-204e8f973c0c44feae786db706cec1f1
ACR-2e08c06c572c41fc80353345b6c520bd
ACR-c34aa9acf9ab422da77a30332506b367
ACR-98e2398472bf43a08598253cb2236381
ACR-066274d99eb440f5becd136a42031e9b
ACR-ce7197cbc9744c5f85588e5ae09ebcab
ACR-53848368c8af43ffb6cfbdacc06b68c2
ACR-a19117b0f1264590bb9dc2eeaae4841c
ACR-edb25150ccd34a178de4a9c525eed0e0
ACR-65177847e9c841588aa43c5ce27d1508
ACR-bf9d28f0328a435d88480037796a5b13
ACR-70eb005b110d41249cf8e419af86b8f9
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
