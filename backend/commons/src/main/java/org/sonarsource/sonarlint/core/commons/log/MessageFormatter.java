/*
ACR-fa16b19651af4fab8de6445c134c7b7b
ACR-bce0103b084243aa94d3ef515f3494cd
ACR-dd1667532f9f4ab0ac7662b2fcd778fa
ACR-89931e7da04446c89c19ee54e8243b60
ACR-401cb8a7a754496cbfcb0dbd59f6d991
ACR-7ad05227927d4c41a2b555f935f3131a
ACR-ebc9f070902e420bb998de879633599b
ACR-ecd932ae3a3e48f982d45f7b63e58b21
ACR-ea3eaaa381434613bca1b55daae8db68
ACR-e927c84b65214d47bb2b4f442b3b6a72
ACR-ac9b21f809434dac9e7e65426b9f9fde
ACR-00c154baea334a20aa69d04558ef0670
ACR-ddd0fc68583341e385fb27543de3ed78
ACR-a236d863f3fe41ffb8ec90641c65681d
ACR-780fdf8f94b5467ea629c70311b1e447
ACR-d6f77b0cbe214897a3ea3b194bf72d86
ACR-658de4bc660e4eec9ad7754668236b70
 */
package org.sonarsource.sonarlint.core.commons.log;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

//ACR-086c1d17be0245f9a428e62917182a73
//ACR-eb465ffe007046f99830c226ba858b12
//ACR-1d88be4d95eb4349b507f9cc3c839a0c
/*ACR-c0d92c00a77c4e60b17cdbc545fbcf8a
ACR-88f21464e3664fcdbf3ab5891de61397
ACR-69a7de7b199044f796ece0b606ba05d8
ACR-b28a7189e10346ddba72f07a11f861ce
ACR-e95479a16dff4011ad8cca29d073fdce
ACR-9be7363cc388478eb9e6de1cfe1f292d
ACR-3f1a7589bd9943208871ec395cc22712
ACR-9e21fcaa117c47d1b01374484d3bc4f5
ACR-85f964e5eb894497bbbc82341be5ac8b
ACR-877e6d1f6a204f17831c5fe55d4fcf98
ACR-8e811aff3ec948ba9f2ebe35da09a0c8
ACR-24f64a82691b46ae9055afe01f48592c
ACR-cb01bf0a11e341a4b30e6e3c9a67f118
ACR-d70cc4eaf0be4afb9b5d3a84d63d3c46
ACR-e5c9579cbb754d69ad88403cf2e5df29
ACR-3d0b278b3eda4afe8f532f8538919eb4
ACR-53432cfed6b14973a75aca930a7f311e
ACR-be31fcff6ca7422bbaa6c639ccdfb79b
ACR-d024c8ba1ac74824803fc968ec587eab
ACR-c57de78182584859ac97dbeb4a68869f
ACR-4feb52a52de142dda96a1702ab4d0211
ACR-3757e2fdd4374d58b7bd214cde5e6f7e
ACR-77bf936b43034c818182b4d29a91bdbe
ACR-439d580c4db2468fbafcc619ebde086f
ACR-039fc1e500ff42f09b5ae80e875a1b34
ACR-6000b0775f0b44e3a854872b9fdecc8d
ACR-707d7be44b324f269e13e7d49928ab58
ACR-46801e085e9a46efb5632798cdc0cf6d
ACR-aa3ec7f65a7d4e289ea97ee371fa9c67
ACR-38c5dfdf5286466b8192df5b0fa43b20
ACR-3837054abdd6461fbcd12f1831c659a3
ACR-661f58fd901d4c48a1b1096b66f3fb97
ACR-d73dd59173984cc09647c65aaae2ef82
ACR-4ab1a2e3e3c444c69d7dbd2b0a2875d1
ACR-f7d34d0e96b142ac917543c56e106537
ACR-9c66b18013ff4107be5cca8a2bcc73ed
ACR-7883768af43b45559423efb5c278a541
ACR-b9fa58b60b6d422fae17fa816abf26df
ACR-6a94725109e24eac9e5d99a81ef8d3e2
ACR-558dbbe457bc47a1b3dc741d220124b5
ACR-954da59d7f5b47648d5a1aa75ab583dc
ACR-318978a561dd49deb02509a79b98d16c
ACR-9420d03ed9e649a7bee52353958313df
ACR-794251e1a051445abb5bfad9ff1b027f
ACR-0b5e0a2d9f064d1588178e34f3e465d5
ACR-aca7d7ccf12340b0ac21ed23bf0c697c
ACR-ff972d9ff39f483aabba8c125651e5f8
ACR-da42fea938094c7495157c61f1e2cf30
ACR-67ab294e05dc406abae685c23a75f196
ACR-59b0d18d32584691a80b268254f3c929
ACR-93d397cd91964514812993b1b487589a
ACR-c57954af479f4a0996414f7d7923a8a3
ACR-2433b87b0f1141aa9abd2ba1c550e403
ACR-e50b0810ffa446438bbf2cb6b48d83cd
ACR-b280abb102c24cd0b191d4c77eb58dbb
ACR-6ec46a732110497f996189ea1ce89269
ACR-79d7ec7b9fa848a89deacf1a6f96e9c7
ACR-55edc121b5c24cf7b41cb49636e774ec
ACR-237a4f8a73724eec9c44394166f15af2
ACR-eb9dfad0b2dd47aabaddbd691d63c2ed
ACR-390d178352f647d285248e66055de20c
ACR-93d29276c21d4daabb41d0fdebb0a189
ACR-e564ac1cfb604cfd9cea3271dd1a9902
ACR-ec7461dd50ee40b7b193cd5b27f473b6
 */
final class MessageFormatter {
  static final char DELIM_START = '{';
  static final char DELIM_STOP = '}';
  static final String DELIM_STR = "{}";
  private static final char ESCAPE_CHAR = '\\';

  private MessageFormatter() {
  }

  /*ACR-7d10b392536f4389a4ff80b2c2c82788
ACR-4a282d0700d04ff1a2c10f2da4a18fc4
ACR-79208d9bd52f4580a28f72b65eaa4f3e
ACR-0ed7e9f3f87340ee8256a6e468c64f91
ACR-baecc31c7d314eb89f029f07c4235a6f
ACR-b95c48d18e8146ebb1b92b4adb8387bb
ACR-573bf1d38d3443fd9d8208fb5e41578a
ACR-84d5b3cbe8334a5a83d211191e402090
ACR-7f9fe62fe5b344b4b2b9d922b63a3acd
ACR-5d2138760ca74f338e7ef6503f95ee09
ACR-65d8188d337242e4ab6a684e2a5106ee
ACR-38bfb08fd9b340bc9d656034cd51ab93
ACR-353b44921fd24882b4560ac8c8b1a99a
ACR-01a2f76bdfb74a31a8ecad251556b6b7
ACR-1498c0fdaed74f43a5ea836c69f6d5c4
ACR-116dbbfd42b043dea5fe5af25e5e4ecc
ACR-cd4db4c2cc804beeae2c79bfac9dafb8
ACR-407aac16c718440785d5c8e18191b443
   */
  public static FormattingTuple format(String messagePattern, Object arg) {
    return arrayFormat(messagePattern, new Object[] {arg});
  }

  /*ACR-8624d8cdf76d43bca69523fc15454a37
ACR-4faf499c2ada4fcf86e5e881f099e72e
ACR-790494d2752342a49ac8db03d22ade4a
ACR-c57baf65b52d4581b1b47688186a53ba
ACR-8936f0d1454146a29a58db223b0aae53
ACR-44487f2deb9248acb3f3d3a8987c98b3
ACR-7a1a55c218614f9fba44d55d72d796b6
ACR-859cefcadb224297b5ca7ac32d58d98e
ACR-78a4898da7a64c40be84b1278f932097
ACR-058c4b27708a4a2e9656e7cb86388f69
ACR-897f5f8a071543fa9eec49cbd740193c
ACR-c72fa65b1bce4e14a5b46655f28bb18c
ACR-c99ceae11802496886ae8e778ae6823b
ACR-1c4ee605e77d4273bca449d8e4d6d739
ACR-af2db486b96b4c66812cfd7714c89d77
ACR-e8461b8b961249baa0a72394edd4a311
ACR-130ea2cca99149ad94df27532f43029a
ACR-dd6a2cdc7ab24e63a9286091fb70f767
ACR-19818aee098d4c7390ef0af125b632e8
ACR-b80dce7bed38475fb91ebea7ee1f9a0c
ACR-c6343735453b4d02bd7dbe23418b17a9
ACR-f52d837611604722adb2bc1ab3e8014c
   */
  public static FormattingTuple format(final String messagePattern, Object arg1, Object arg2) {
    return arrayFormat(messagePattern, new Object[] {arg1, arg2});
  }

  public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
    var throwableCandidate = MessageFormatter.getThrowableCandidate(argArray);
    var args = argArray;
    if (throwableCandidate != null) {
      args = MessageFormatter.trimmedCopy(argArray);
    }
    return arrayFormat(messagePattern, args, throwableCandidate);
  }

  public static FormattingTuple arrayFormat(@Nullable final String messagePattern, @Nullable final Object[] argArray, @Nullable Throwable throwable) {

    if (messagePattern == null) {
      return new FormattingTuple(null, throwable);
    }

    if (argArray == null) {
      return new FormattingTuple(messagePattern);
    }

    var i = 0;
    int j;
    //ACR-41d975592262459fab21e6299b257974
    var sbuf = new StringBuilder(messagePattern.length() + 50);

    int L;
    for (L = 0; L < argArray.length; L++) {

      j = messagePattern.indexOf(DELIM_STR, i);

      if (j == -1) {
        //ACR-f6eb3185e4c24a7caf63f530f6a9adcf
        if (i == 0) { //ACR-fb52c9278fde47aab884885286281402
          return new FormattingTuple(messagePattern, throwable);
        } else { //ACR-d7f46c92e3904ed9a12458826019f339
          //ACR-4b83750049b34738a4951ed679433b7d
          sbuf.append(messagePattern, i, messagePattern.length());
          return new FormattingTuple(sbuf.toString(), throwable);
        }
      } else {
        if (isEscapedDelimiter(messagePattern, j)) {
          if (!isDoubleEscaped(messagePattern, j)) {
            L--; //ACR-1f872092097740319698d12607e1d0a8
            sbuf.append(messagePattern, i, j - 1);
            sbuf.append(DELIM_START);
            i = j + 1;
          } else {
            //ACR-775da2cf23094a2e804150d19edc4207
            //ACR-9bf71d9de4514457bd02c3aad6be2a9a
            //ACR-943c261bdb994f33b9c380c45531d853
            sbuf.append(messagePattern, i, j - 1);
            deeplyAppendParameter(sbuf, argArray[L], new HashMap<>());
            i = j + 2;
          }
        } else {
          //ACR-930c41730e2b4f9d8b18ea946edd750f
          sbuf.append(messagePattern, i, j);
          deeplyAppendParameter(sbuf, argArray[L], new HashMap<>());
          i = j + 2;
        }
      }
    }
    //ACR-1d653bbcc3604791a05ed5e51f2a2ad9
    sbuf.append(messagePattern, i, messagePattern.length());
    return new FormattingTuple(sbuf.toString(), throwable);
  }

  static boolean isEscapedDelimiter(String messagePattern, int delimiterStartIndex) {
    if (delimiterStartIndex == 0) {
      return false;
    }
    var potentialEscape = messagePattern.charAt(delimiterStartIndex - 1);
    return potentialEscape == ESCAPE_CHAR;
  }

  static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
    return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
  }

  //ACR-109719249edc425aa56002d296a78e56
  private static void deeplyAppendParameter(StringBuilder sbuf, @Nullable Object o, Map<Object[], Object> seenMap) {
    if (o == null) {
      sbuf.append("null");
      return;
    }
    if (!o.getClass().isArray()) {
      safeObjectAppend(sbuf, o);
    } else {
      //ACR-385244c45f5f4dc2a671843d93e86dc3
      //ACR-1729543ce5784ef690b2f8676fd2cf57
      if (o instanceof boolean[] booleans) {
        booleanArrayAppend(sbuf, booleans);
      } else if (o instanceof byte[] bytes) {
        byteArrayAppend(sbuf, bytes);
      } else if (o instanceof char[] chars) {
        charArrayAppend(sbuf, chars);
      } else if (o instanceof short[] shorts) {
        shortArrayAppend(sbuf, shorts);
      } else if (o instanceof int[] ints) {
        intArrayAppend(sbuf, ints);
      } else if (o instanceof long[] longs) {
        longArrayAppend(sbuf, longs);
      } else if (o instanceof float[] floats) {
        floatArrayAppend(sbuf, floats);
      } else if (o instanceof double[] doubles) {
        doubleArrayAppend(sbuf, doubles);
      } else {
        objectArrayAppend(sbuf, (Object[]) o, seenMap);
      }
    }
  }

  private static void safeObjectAppend(StringBuilder sbuf, Object o) {
    try {
      var oAsString = o.toString();
      sbuf.append(oAsString);
    } catch (Throwable t) {
      sbuf.append("[FAILED toString()]");
    }
  }

  private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
    sbuf.append('[');
    if (!seenMap.containsKey(a)) {
      seenMap.put(a, null);
      final var len = a.length;
      for (var i = 0; i < len; i++) {
        deeplyAppendParameter(sbuf, a[i], seenMap);
        if (i != len - 1) {
          sbuf.append(", ");
        }
      }
      //ACR-4c59c30224dd4388bb07c7dd012b3e10
      seenMap.remove(a);
    } else {
      sbuf.append("...");
    }
    sbuf.append(']');
  }

  private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void charArrayAppend(StringBuilder sbuf, char[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void intArrayAppend(StringBuilder sbuf, int[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void longArrayAppend(StringBuilder sbuf, long[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
    sbuf.append('[');
    final var len = a.length;
    for (var i = 0; i < len; i++) {
      sbuf.append(a[i]);
      if (i != len - 1) {
        sbuf.append(", ");
      }
    }
    sbuf.append(']');
  }

  /*ACR-37d06d9a514c4a2389dea0136bf4851e
ACR-2f62a485094740b78ad1dcb270bda971
ACR-34500f2001fd45d39dcaaec8ac591db3
ACR-28a0b0e5084847a39130aa03c40d2ec7
ACR-fe24e5979e1e49069b3fe2fbc74b492d
ACR-b4e570be09254aa099eac427e8cae54c
ACR-ca0a1a55bb094fc99dd8bb6ef1ca304b
   */
  public static Throwable getThrowableCandidate(final Object[] argArray) {
    return NormalizedParameters.getThrowableCandidate(argArray);
  }

  /*ACR-37d211cb795a472186a60a460cd445ca
ACR-1c79ad3a26564b159bfa1640ef42bec5
ACR-56d93e47fd16464182aa5c048b6d8bf4
ACR-ee24d5610a534c6a804eedeb934ecac6
ACR-5d80cdf0ba0d479cb482bda1132811c0
ACR-3c3c9241db884832bcd9848126db0a3d
ACR-30c5b5b538ce44a58a8da513badc8050
   */
  public static Object[] trimmedCopy(final Object[] argArray) {
    return NormalizedParameters.trimmedCopy(argArray);
  }

}
