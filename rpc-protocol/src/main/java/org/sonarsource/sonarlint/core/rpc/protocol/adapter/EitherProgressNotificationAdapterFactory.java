/*
ACR-36016d84b13e4e33b3a18eaf02a55350
ACR-fbd1552c42a04422bc3f0d383728998e
ACR-ae5db180d5d54d28bae68ac86e925d05
ACR-dd755655127c4e57a696d6650c9c70db
ACR-2d7cf185831b493aac4134218438a3d9
ACR-f90dfdc676de4d9b9f65f7269e8c8f86
ACR-9a0c3f09739249b4abf64ecde496b524
ACR-060909fc712a44008bcf31b38d4acda5
ACR-e743a08661754a2a952cce34c66de17a
ACR-a0aafeb95e50416388b6031576678800
ACR-de3a452fc54045afa5145faa06934b83
ACR-5caa083f31714849b68e4c3c70b79805
ACR-21d86c7415704cd085c12b6cfe2ff1c5
ACR-ff48a5415f054315af313b1770ce721b
ACR-8c3d8bd9a99347b0b9ba675aa9d6e828
ACR-2f2a3eb70eea4b53b24c58d6a34b7fb5
ACR-7c79364dad94487bb2a73a6f77f0b5ad
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
