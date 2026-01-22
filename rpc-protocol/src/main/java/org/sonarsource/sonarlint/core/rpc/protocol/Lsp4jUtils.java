/*
ACR-e5df72dd7f87433ba5af4f2816af95e4
ACR-5ace3d239adf449ebe506952f5c057d5
ACR-2f5c241969aa4a6d8083cfad8ba21c67
ACR-d1957c5b1c1d44f8a3239de691cc12a4
ACR-d438ff0b9ed2468882c8b567cfeafaf2
ACR-0b8cf2028d914192964498c86c16a1f8
ACR-6ab591cc1e8e4c06bcddd7ca96fe8ea3
ACR-768fe64779c34242be012a243068fbad
ACR-fde69da414344a30bdd77174ac9e029a
ACR-54728544f64242ecba1bbec1c2b5a881
ACR-2167df8b33144230a37e18dfab69e482
ACR-6f04ebe3b27a4f78a7b6e78d8a777730
ACR-6361e6aba9db48f58fb018c5e25a683d
ACR-f2519db6cf9443a6b64f7b89c196650c
ACR-e8f0eae6237c4074b05ec2b387e0b142
ACR-29ae08fb9b6042bab61a53ec8ec5bdc4
ACR-9cccbd3d1df544ccafad664ad96b18ea
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class Lsp4jUtils {

  private Lsp4jUtils() {
    //ACR-e72cd421fcf74af49479cf95381f71f3
  }

  /*ACR-8b0adc12a58e4c4f9319a9ed0303ecd5
ACR-57eda581d4db4fec974bf5a0b636d8df
   */
  public static boolean isEither(Type type) {
    if (type instanceof ParameterizedType) {
      return isEither(((ParameterizedType) type).getRawType());
    }
    if (type instanceof Class) {
      return Either.class.isAssignableFrom((Class<?>) type);
    }
    return false;
  }
}
