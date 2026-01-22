/*
ACR-586137a80ff34d7d98475d1f843e8398
ACR-b8101dd90b0842fdb4be31923464f65a
ACR-19fd3149c0ef43aba8f6de6ce2f598db
ACR-4ff9884c14dc4782ba5fd0f2888658f8
ACR-dd9e0d24608345e49fd1ea9c9874f905
ACR-cc8e1ef432a24e278f6b8f02f187e048
ACR-49f2639c258a4b9ab7d7d6e9a7dcf070
ACR-174210691c5e4fc6a1da9413d22e8755
ACR-fba51a3827f04ed188cccc63935c3695
ACR-b5145baa4fe843bc812e030e61d05b97
ACR-f79b84aeb7dc4e429d207ec61ac9379d
ACR-7b79b1bca8fd413f844e2c5164965614
ACR-19f773ecc05548c4abd3415061b69e76
ACR-00481adffae04f84ae072ccdfb3ba475
ACR-d187071eb6e340c8b1815bcf035525b0
ACR-446d25913a1941a6a37582d8ce03d208
ACR-e3db139571e44bdca0633a2cf52ab3ea
 */
package org.sonarsource.sonarlint.core.commons.log;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

//ACR-0397eb5aed1148289c974703043de4c2
//ACR-ee3484d053d94f41a6d77219293c213e
//ACR-7e662035dfd64eaabe0bef3b228510a0
/*ACR-4e7f27d23ce14cf98c6fdcf86c3bebbf
ACR-a1017702367448fa9348076bb9ab32c3
ACR-94db3c2c82a24263be0d3b8aff7a2fec
ACR-b3a0d40d0ece47bf8a3b0d82e78dd474
ACR-fc5344f430b74cbf985e05a81ee28e37
ACR-4845d9bed6064933a7f81b468241182d
ACR-5e95e406a74a4616ab39aea17ab62c85
ACR-b16b855408f94e4b87e59931b7ebc1ec
ACR-c450ac0cc14a4b7581f4ee008d296708
ACR-d68e1fb87802424ab2182c51264ee2e8
ACR-dc23fbd6d73741f5b7e77d53f33a3dc9
ACR-8de61104054d4573acacd797f6a58e22
ACR-0be6c741336b465d929f975f6d3dc67d
ACR-dcf15646723d4ab7bafd630350b1a9b5
ACR-a8e5fff2bc7544c2b4899717ea4a7934
ACR-9961f75205834fc3ae3825e90643c5b7
ACR-e260b812b96a4e958d00224c95deced1
ACR-b26c608beff049b3a8fcb8f76b7401a6
ACR-882ad3be864c4a4b9a171c5786b2e731
ACR-83238b7928564f029fe0c7ee455d97dc
ACR-a9b36e35ad3a44f790a7752c5c1000fe
ACR-bcb58001546148efbb19b67a2042e534
ACR-e12993dd7855488f9dcabbd0ea077764
ACR-9b99fa19ef2940eca36afcadb19cec51
ACR-f4b18958fba04a62b017d240ad04db35
ACR-7d11172c7f804e5693955926372d5c4f
ACR-c125aa11f0e641a08d36fdcf9360a2bb
ACR-fadecee3c60c4bc984209d81095bf9a4
ACR-68822916c638495dbb9b7b5c3a4f1e51
ACR-1afa7df2b0b54ad9899c0676eb891325
ACR-4d06e6b00fc141caa934a6b36ea31116
ACR-bf49806c91e540cf8103ba18d1efa4a6
ACR-9a67cf37c71246c4a66ade59a1305d75
ACR-95d9f9ef11c74fad8e506c48b86179be
ACR-75dabc42a5ca4879b04ba5bb3f7a910d
ACR-caa96667403d47ed9a79043bf4505a0d
ACR-acd024cb301a4410b5e6d1366dc7453e
ACR-f4d41dde04ec45029061f16fb9790d99
ACR-e8693e0f0d294acc9891bc4655fbf350
ACR-a7582f834e024820800929e3aa035e34
ACR-86fe16337cc2488da3115c1b477fce9a
ACR-10ddc948f2334c8caaedba8f602829b0
ACR-01bb5751e0504d679db9acfd66d742ba
ACR-784b195ecf554e9a877845538b7f914b
ACR-d843eee0e9094537a893123ca153d0af
ACR-c67f8616fddc4287840fc35a3fbfb45e
ACR-babcedbb889243a5a5cd13d2dc658ded
ACR-590bbf21296744d29ccda7f6085fd410
ACR-e5dfac88c2e346ffa79df7c2d598087f
ACR-ece131b54017445082aa35aea4eee06b
ACR-e0e37f887d9440878c5ee563c28ff0f1
ACR-e222929d85ac4361a153bef0e54b0426
ACR-917cf78a184b40c6be71754e43c9cd40
ACR-d3f75ade0f774849a07b884e41e64c6a
ACR-6eaa7e9ddc49436d8d680bebfef9c9de
ACR-b967e4fd29b34bf7b8827c4ae71d6d06
ACR-ca79eaa21dc64189a282bd88eefabd24
ACR-71f8933d53104e4caff455bceab5071d
ACR-7124b5dda6af44738d9edcfaf1b642de
ACR-4087d86c7a054a539f734fa436566949
ACR-f8c37ea0302c442d9b7857d663ebb788
ACR-a3edec63b1c94809bea608f7cca51098
ACR-40777e0b1a0042b7834f5dbf29362077
ACR-d687ebbed2ee41ea93a9b949a107e315
 */
final class MessageFormatter {
  static final char DELIM_START = '{';
  static final char DELIM_STOP = '}';
  static final String DELIM_STR = "{}";
  private static final char ESCAPE_CHAR = '\\';

  private MessageFormatter() {
  }

  /*ACR-d07d53124315424c9e6e6ce0fc69a55c
ACR-3186ccff91ff46e28f0169d6a8869d13
ACR-b91f185985774bfaba6ced593207042f
ACR-967037c54a0d41d5b86af34e097fe7c7
ACR-c68f75e39ada4824a86b99917e5ea173
ACR-6e268c25ec014ec88ac5b63070aa9f1b
ACR-77848e042bb4431bbe66b73374f7ca68
ACR-30803660302e492894797202a21ac75d
ACR-52843241906947c1a8d0fdab6de1acb4
ACR-6698b6970075495ead26609d5c65a333
ACR-b9a8a64dc83b40dbb366c4e405df13f2
ACR-613634b91d0e4bc989ec826c217cb370
ACR-fe40377b1c634766a6e9aa0efee24472
ACR-e4dba2b638f346f3a9c80ee17ac26002
ACR-a603def6539c4fe69eb1fbf8c876ced3
ACR-33eeb1634b2643ae80b27e8447382cb7
ACR-e9d0753efb664aea8824548640878fd7
ACR-09af9593763a417ab4fd3ff0235de54b
   */
  public static FormattingTuple format(String messagePattern, Object arg) {
    return arrayFormat(messagePattern, new Object[] {arg});
  }

  /*ACR-cd935a61dccf44f6ac4eee7bfa58d2b0
ACR-d8ebac1701644bc6a73a0c5a352c33b0
ACR-c621d6e953b744c788334a34a24e6706
ACR-7f8a37e6f03a4250919616c8a477c017
ACR-57466ae44e1d417e890fce138cd9a9a9
ACR-0ef300fb4f2843fb9328cc750dc12a1a
ACR-a2e0b0fb369b4feeb828405a71cede6e
ACR-75303730d09f4e8da386d3609e216e3e
ACR-7fd5757b9e894247b501c056d808d2a5
ACR-c4794f872de24400b363a346054ff162
ACR-96c2c3e5e9e14b81988ad6d92300592f
ACR-9f8cbce3375d45ccb973032bb0c7cea3
ACR-946e1197ef0e48038868d75a0c11de6c
ACR-7fe4c4d8617b463f82fd12653d7fcb95
ACR-eebd77e4df3e4f5a9270ae00745eba07
ACR-fe0d9489289c4f648180b67d5d10b4b9
ACR-2c42acb8d9294f0ba51103d5343be4c0
ACR-0aeb80e7b5244a8b99f5e2babd7eb237
ACR-7ea6797113a64d569713c9608fa52c26
ACR-aa479a05dd624f18a6b3d22f42be46b1
ACR-214de549b41d465080ddac5281520b35
ACR-886e1e9edc724007b8088dd400d2e8dc
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
    //ACR-52a9b509652043ce8030c35280912ccc
    var sbuf = new StringBuilder(messagePattern.length() + 50);

    int L;
    for (L = 0; L < argArray.length; L++) {

      j = messagePattern.indexOf(DELIM_STR, i);

      if (j == -1) {
        //ACR-ab23fd2c75f2428fb7e435bcbd648a7f
        if (i == 0) { //ACR-16b18af1ea6b471cb57c547e551a7149
          return new FormattingTuple(messagePattern, throwable);
        } else { //ACR-766c37ed51b74170a02e826e976c48d7
          //ACR-119a607741214dd1a803a9f2beeab29f
          sbuf.append(messagePattern, i, messagePattern.length());
          return new FormattingTuple(sbuf.toString(), throwable);
        }
      } else {
        if (isEscapedDelimiter(messagePattern, j)) {
          if (!isDoubleEscaped(messagePattern, j)) {
            L--; //ACR-fb338873a3464d3db9f8dfc10e69d624
            sbuf.append(messagePattern, i, j - 1);
            sbuf.append(DELIM_START);
            i = j + 1;
          } else {
            //ACR-666a5a2ad6b1469f9b654863017925b0
            //ACR-94fc1ac272554c7fa57540eb5ed16d4e
            //ACR-854d5c506b024922a86423f6324d6776
            sbuf.append(messagePattern, i, j - 1);
            deeplyAppendParameter(sbuf, argArray[L], new HashMap<>());
            i = j + 2;
          }
        } else {
          //ACR-21a656953f7f4392a3967ab65b8121e9
          sbuf.append(messagePattern, i, j);
          deeplyAppendParameter(sbuf, argArray[L], new HashMap<>());
          i = j + 2;
        }
      }
    }
    //ACR-66161e0fec054980a1b499d5d32943d5
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

  //ACR-6752bcda8b4443a29d16784a18d461cd
  private static void deeplyAppendParameter(StringBuilder sbuf, @Nullable Object o, Map<Object[], Object> seenMap) {
    if (o == null) {
      sbuf.append("null");
      return;
    }
    if (!o.getClass().isArray()) {
      safeObjectAppend(sbuf, o);
    } else {
      //ACR-50ed093a4f0e455c848e958153657498
      //ACR-dab41d6cac26431fba63987e1bd11e69
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
      //ACR-20a7aef5fcaa4b23aa8ed66b14f709c8
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

  /*ACR-859e331918824b2fb95f208c753eea01
ACR-2437301f151742ff9b7954e6619aa6af
ACR-34db4420d8c44e78bbf4a0084314a143
ACR-7e2a3e981bbc4666b36f3df9a447e734
ACR-16e13ca5da5b4534adc3c248e4dce905
ACR-622da5f9e7734189b08747b8d967743b
ACR-b3e8bc5f60444171ba7b232abde637fa
   */
  public static Throwable getThrowableCandidate(final Object[] argArray) {
    return NormalizedParameters.getThrowableCandidate(argArray);
  }

  /*ACR-f5520bdcafb24863be5d5e50d35fda8e
ACR-ead8a3ef2c5b4374a5f6e7b12fab2520
ACR-42a3be7d2def484db920882c3e845ed9
ACR-4247d55e6e7047bbb279d4168b0c02ff
ACR-d289863017df4114bf71d8ba2113d9fe
ACR-db8b8f2395204d4cbe77857c95653dc8
ACR-67b5ff09fafa437e9437546d75d3385c
   */
  public static Object[] trimmedCopy(final Object[] argArray) {
    return NormalizedParameters.trimmedCopy(argArray);
  }

}
