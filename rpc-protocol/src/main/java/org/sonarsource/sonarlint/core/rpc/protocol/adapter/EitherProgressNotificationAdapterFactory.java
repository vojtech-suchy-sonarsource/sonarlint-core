/*
ACR-30118de6dd7b45d9955a77fbd1accdea
ACR-3c20168a80cd4c719c04811ecd25e3a1
ACR-e2265fb7f88f46e4ab6e93fa212c642e
ACR-9ee2f17cd49141339ab2faf6ee40a2c4
ACR-6cc26ad5fcb449c4a3f03ce3528263af
ACR-2201e3c214bf4e64ab8ae513b1053536
ACR-1d03c99d38334977bfe2cd334491dbec
ACR-236ea0b5932e401a900d6f5f7a8d5427
ACR-78cc1b650a5546e39f634de7b9500914
ACR-1aa67942fe9143ee8425682c49519f03
ACR-3b768aa170304b2099bd2978849ba556
ACR-52f88839075a48828f0af2891c24bb3c
ACR-2562038ad20c4fa39ca36f579265e698
ACR-e4deafb304d741999c00764d0df55931
ACR-d58043add6c34fdba9e4c161d971b87a
ACR-137a5109b63143e5a2e31f14032d425e
ACR-34e34db0c1764a71b74d30e8516143fc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ProgressEndNotification;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ProgressUpdateNotification;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherProgressNotificationAdapterFactory extends CustomEitherAdapterFactory<ProgressUpdateNotification, ProgressEndNotification> {

  private static final TypeToken<Either<ProgressUpdateNotification, ProgressEndNotification>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherProgressNotificationAdapterFactory() {
    super(ELEMENT_TYPE, ProgressUpdateNotification.class, ProgressEndNotification.class, new EitherTypeAdapter.PropertyChecker("percentage"));
  }
}
