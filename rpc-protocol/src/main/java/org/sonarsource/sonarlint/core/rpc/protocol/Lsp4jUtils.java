/*
ACR-e3124be4c40f462fa4a46efd78ce4ff9
ACR-c7ab252ae1524c30871fe8ea70316db7
ACR-498e6156887d413cb2f0143be584ea2e
ACR-12f651f811b845729424b094504959a9
ACR-2b0b37da65224d44a68834382434dfb0
ACR-8c1f94c7b2e74942a5614431de3bd0b2
ACR-bcfe6aea934c427eb30205d72e1dfd9d
ACR-96d05177c21d41f6af146fdcfdd32d43
ACR-cb41fba574d6472b9f0bdc6e44d74376
ACR-4eef21d0a20f47a1b77f946cd3803202
ACR-cca0fde72088469996cace39f3eaf0e8
ACR-8c69563eb2f74ae09607ea1c225db888
ACR-47caa21b87a24750a4c2a28322e2b239
ACR-eb77382ebd6e4581b428e1dcd608fc75
ACR-3601c4c921fb43c493f1256100a1569e
ACR-d2160bfb3cfe484598ede33271bab4eb
ACR-876894cc65084493b9f8c9bdce79fc66
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class Lsp4jUtils {

  private Lsp4jUtils() {
    //ACR-7b8de23aefa24391ad851de3917d7f91
  }

  /*ACR-4a10c3768b7d4916bc12801a9746f041
ACR-d1d3ade8bde54c1d8bd69eaa9bbf0de4
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
