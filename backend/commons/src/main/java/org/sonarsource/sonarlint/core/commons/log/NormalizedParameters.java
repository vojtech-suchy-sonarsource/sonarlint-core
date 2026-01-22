/*
ACR-9415a4d90f10456392101dda160239d5
ACR-9ebbd8df8c1e47689940815b8eeb7b56
ACR-cb09e9ab386145c5b47c8f5debf6cedc
ACR-625bd80bf2ac4e6aa61676ac021146bd
ACR-d03eef382f264973afb8a11dc9c19374
ACR-daafddc585a54aaab4edb941c80f0afc
ACR-b7dc15605e9e43f2be62b66883b1f3fa
ACR-48067a83e0b94c45916b863a3bf5e09f
ACR-6493db9f940c43588436718103a2fdfc
ACR-383ba315fb324ba09b8cbcc620cd0c3c
ACR-3f7a010d13ce455cbce52f20a6b7e52d
ACR-b95b50b8befc4529ab158e96a84d15b1
ACR-9b96658ffe7f47ddbe8fbf0a5d4b233f
ACR-953556e1b1f7498ab495d1c88215ebf1
ACR-df39235b08c54880b24da81bd9c5c6f6
ACR-8aab323999b444609386c20337783496
ACR-224a215d3f3a464786a003766830dfa6
 */
package org.sonarsource.sonarlint.core.commons.log;

import javax.annotation.Nullable;

/*ACR-2b31250f810d4ede8010bb3033647d74
ACR-f3ad822ae2574e41b3748b86703c268d
ACR-90089095453c45cc8f9f6276fdbdb5d7
ACR-326028be62be40b29af3f079b0f13c2d
ACR-59400b7b690c4c2295d3f453bc3d20b4
ACR-46c75e874c16443eb4cfa76ade4f92af
ACR-814c5ef4a9bc4bc4968d2963bea6ed15
 */
class NormalizedParameters {

  private NormalizedParameters() {
  }

  /*ACR-e87eb3262baa4fdcb1a926009758d7dd
ACR-3ddbdf0282814838b9468e7a2d5cfe1c
ACR-88e8f7f04f794fb69923178ebe1e0601
ACR-c57ca597878f479f92d4d21c2d9f3864
ACR-6be523b85e64450eba119256a2c1d7c5
ACR-6af1bf3f09a24863940bd5ee704e0db6
ACR-838c0ed83f7d443fad1b0a38d8852a09
ACR-ad126a701e034abc9dd48a3f7d9ad414
   */
  public static Throwable getThrowableCandidate(@Nullable final Object[] argArray) {
    if (argArray == null || argArray.length == 0) {
      return null;
    }

    final var lastEntry = argArray[argArray.length - 1];
    if (lastEntry instanceof Throwable throwable) {
      return throwable;
    }

    return null;
  }

  /*ACR-83b912ab0d694867a239bfd9e96558c1
ACR-6de0d08b88c44e1dafbb0d5b9ccb94f9
ACR-a8e97596aec64c388a845b5011846532
ACR-a7c7b3c9825a40a284d4b60c97c775f2
ACR-0dd281f958844cdf8716a5aa873517ab
ACR-90eab0ece5fa4610b8c0d7ccf5fc14fd
   */
  public static Object[] trimmedCopy(@Nullable final Object[] argArray) {
    if (argArray == null || argArray.length == 0) {
      throw new IllegalStateException("non-sensical empty or null argument array");
    }

    final var trimmedLen = argArray.length - 1;

    var trimmed = new Object[trimmedLen];

    if (trimmedLen > 0) {
      System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
    }

    return trimmed;
  }

}
