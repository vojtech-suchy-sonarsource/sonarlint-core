/*
ACR-f765992ed87d4fe1b870a841d23d2e66
ACR-3c479ca5340441d98ddaf197f4f11fb3
ACR-020a0775186949f6b008c8eed82ea3d8
ACR-9a801a92e6694f60a3a231b573146902
ACR-6dec1635877d4060a553d90b16d0108a
ACR-da9b6f3b3f2047eca54a1fe146bf3190
ACR-562297b661e24cae88dc418a14d3a286
ACR-60f1fdbeadf047608520d38cba47b3dc
ACR-bda939f766484f10805e84d8afa6edae
ACR-742fd808b1ce4b38a7e49e10a9cc1851
ACR-38042fcaf96448a4b05f95168b0562ac
ACR-7e1eca8444f44467a9c493342aac224c
ACR-e7e542281fcb438eb244eeedffd35b66
ACR-7f98447cd1684e6c92c5f14cf7f85191
ACR-0bd8f2fb0745415592366569a38fa57c
ACR-a8327ef26d724fadb575e8511a6a2234
ACR-17d9547b90914046b7247c9a4d3faae5
 */
package org.sonarsource.sonarlint.core.commons.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/*ACR-b92e131ca9934cc4a7cce36c4cba50d9
ACR-ee563d5367b64773a81fbeb95a05d50c
 */
class MessageFormatterTests {

  Integer i1 = 1;
  Integer i2 = 2;
  Integer i3 = 3;
  Integer[] ia0 = new Integer[] {i1, i2, i3};
  Integer[] ia1 = new Integer[] {10, 20, 30};

  String result;

  @Test
  void testNull() {
    result = MessageFormatter.format(null, i1).getMessage();
    assertNull(result);
  }

  @Test
  void testParamaterContainingAnAnchor() {
    result = MessageFormatter.format("Value is {}.", "[{}]").getMessage();
    assertEquals("Value is [{}].", result);

    result = MessageFormatter.format("Values are {} and {}.", i1, "[{}]").getMessage();
    assertEquals("Values are 1 and [{}].", result);
  }

  @Test
  void nullParametersShouldBeHandledWithoutBarfing() {
    result = MessageFormatter.format("Value is {}.", null).getMessage();
    assertEquals("Value is null.", result);

    result = MessageFormatter.format("Val1 is {}, val2 is {}.", null, null).getMessage();
    assertEquals("Val1 is null, val2 is null.", result);

    result = MessageFormatter.format("Val1 is {}, val2 is {}.", i1, null).getMessage();
    assertEquals("Val1 is 1, val2 is null.", result);

    result = MessageFormatter.format("Val1 is {}, val2 is {}.", null, i2).getMessage();
    assertEquals("Val1 is null, val2 is 2.", result);

    result = MessageFormatter.arrayFormat("Val1 is {}, val2 is {}, val3 is {}", new Integer[] {null, null, null}).getMessage();
    assertEquals("Val1 is null, val2 is null, val3 is null", result);

    result = MessageFormatter.arrayFormat("Val1 is {}, val2 is {}, val3 is {}", new Integer[] {null, i2, i3}).getMessage();
    assertEquals("Val1 is null, val2 is 2, val3 is 3", result);

    result = MessageFormatter.arrayFormat("Val1 is {}, val2 is {}, val3 is {}", new Integer[] {null, null, i3}).getMessage();
    assertEquals("Val1 is null, val2 is null, val3 is 3", result);
  }

  @Test
  void verifyOneParameterIsHandledCorrectly() {
    result = MessageFormatter.format("Value is {}.", i3).getMessage();
    assertEquals("Value is 3.", result);

    result = MessageFormatter.format("Value is {", i3).getMessage();
    assertEquals("Value is {", result);

    result = MessageFormatter.format("{} is larger than 2.", i3).getMessage();
    assertEquals("3 is larger than 2.", result);

    result = MessageFormatter.format("No subst", i3).getMessage();
    assertEquals("No subst", result);

    result = MessageFormatter.format("Incorrect {subst", i3).getMessage();
    assertEquals("Incorrect {subst", result);

    result = MessageFormatter.format("Value is {bla} {}", i3).getMessage();
    assertEquals("Value is {bla} 3", result);

    result = MessageFormatter.format("Escaped \\{} subst", i3).getMessage();
    assertEquals("Escaped {} subst", result);

    result = MessageFormatter.format("{Escaped", i3).getMessage();
    assertEquals("{Escaped", result);

    result = MessageFormatter.format("\\{}Escaped", i3).getMessage();
    assertEquals("{}Escaped", result);

    result = MessageFormatter.format("File name is {{}}.", "App folder.zip").getMessage();
    assertEquals("File name is {App folder.zip}.", result);

    //ACR-b09fb997e43b46cbb38cb82624f0600b
    result = MessageFormatter.format("File name is C:\\\\{}.", "App folder.zip").getMessage();
    assertEquals("File name is C:\\App folder.zip.", result);
  }

  @Test
  void testTwoParameters() {
    result = MessageFormatter.format("Value {} is smaller than {}.", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than 2.", result);

    result = MessageFormatter.format("Value {} is smaller than {}", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than 2", result);

    result = MessageFormatter.format("{}{}", i1, i2).getMessage();
    assertEquals("12", result);

    result = MessageFormatter.format("Val1={}, Val2={", i1, i2).getMessage();
    assertEquals("Val1=1, Val2={", result);

    result = MessageFormatter.format("Value {} is smaller than \\{}", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than {}", result);

    result = MessageFormatter.format("Value {} is smaller than \\{} tail", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than {} tail", result);

    result = MessageFormatter.format("Value {} is smaller than \\{", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than \\{", result);

    result = MessageFormatter.format("Value {} is smaller than {tail", i1, i2).getMessage();
    assertEquals("Value 1 is smaller than {tail", result);

    result = MessageFormatter.format("Value \\{} is smaller than {}", i1, i2).getMessage();
    assertEquals("Value {} is smaller than 1", result);
  }

  @Test
  void testExceptionIn_toString() {
    Object o = new Object() {
      @Override
      public String toString() {
        throw new IllegalStateException("a");
      }
    };
    result = MessageFormatter.format("Troublesome object {}", o).getMessage();
    assertEquals("Troublesome object [FAILED toString()]", result);

  }

  @Test
  void testNullArray() {
    var msg0 = "msg0";
    var msg1 = "msg1 {}";
    var msg2 = "msg2 {} {}";
    var msg3 = "msg3 {} {} {}";

    Object[] args = null;

    result = MessageFormatter.arrayFormat(msg0, args).getMessage();
    assertEquals(msg0, result);

    result = MessageFormatter.arrayFormat(msg1, args).getMessage();
    assertEquals(msg1, result);

    result = MessageFormatter.arrayFormat(msg2, args).getMessage();
    assertEquals(msg2, result);

    result = MessageFormatter.arrayFormat(msg3, args).getMessage();
    assertEquals(msg3, result);
  }

  //ACR-4106e494e7f44d318c462ae574643133
  @Test
  void testArrayFormat() {
    result = MessageFormatter.arrayFormat("Value {} is smaller than {} and {}.", ia0).getMessage();
    assertEquals("Value 1 is smaller than 2 and 3.", result);

    result = MessageFormatter.arrayFormat("{}{}{}", ia0).getMessage();
    assertEquals("123", result);

    result = MessageFormatter.arrayFormat("Value {} is smaller than {}.", ia0).getMessage();
    assertEquals("Value 1 is smaller than 2.", result);

    result = MessageFormatter.arrayFormat("Value {} is smaller than {}", ia0).getMessage();
    assertEquals("Value 1 is smaller than 2", result);

    result = MessageFormatter.arrayFormat("Val={}, {, Val={}", ia0).getMessage();
    assertEquals("Val=1, {, Val=2", result);

    result = MessageFormatter.arrayFormat("Val={}, {, Val={}", ia0).getMessage();
    assertEquals("Val=1, {, Val=2", result);

    result = MessageFormatter.arrayFormat("Val1={}, Val2={", ia0).getMessage();
    assertEquals("Val1=1, Val2={", result);
  }

  @Test
  void testArrayValues() {
    var p0 = i1;
    var p1 = new Integer[] {i2, i3};

    result = MessageFormatter.format("{}{}", p0, p1).getMessage();
    assertEquals("1[2, 3]", result);

    //ACR-613c2b3b78094334a0c0312ec8aa4289
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", p1}).getMessage();
    assertEquals("a[2, 3]", result);

    //ACR-9b3e4b978f054764ae106a3300984307
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new byte[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-a45ffa9c2f0841988c816ae87a1b54ae
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new int[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-f8319f921af344398f0fbcc8b243e8ca
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new float[] {1, 2}}).getMessage();
    assertEquals("a[1.0, 2.0]", result);

    //ACR-e8e0aa02be2040a78dfe8ad1cc934ef8
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new double[] {1, 2}}).getMessage();
    assertEquals("a[1.0, 2.0]", result);

    //ACR-88d5b31a4e6744728a11fbab71659da6
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new boolean[] {true, false}}).getMessage();
    assertEquals("a[true, false]", result);

    //ACR-b1a00d8914d24d7d83f07227c604c909
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new short[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-b9a925f0c28844e8a146de0d2a2e0274
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new char[] {'a', 'b'}}).getMessage();
    assertEquals("a[a, b]", result);

    //ACR-785e9f67811743c893102d5f81c59f5e
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new long[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

  }

  @Test
  void testMultiDimensionalArrayValues() {
    var multiIntegerA = new Integer[][] {ia0, ia1};
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", multiIntegerA}).getMessage();
    assertEquals("a[[1, 2, 3], [10, 20, 30]]", result);

    var multiIntA = new int[][] {{1, 2}, {10, 20}};
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", multiIntA}).getMessage();
    assertEquals("a[[1, 2], [10, 20]]", result);

    var multiFloatA = new float[][] {{1, 2}, {10, 20}};
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", multiFloatA}).getMessage();
    assertEquals("a[[1.0, 2.0], [10.0, 20.0]]", result);

    var multiOA = new Object[][] {ia0, ia1};
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", multiOA}).getMessage();
    assertEquals("a[[1, 2, 3], [10, 20, 30]]", result);

    var _3DOA = new Object[][][] {multiOA, multiOA};
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", _3DOA}).getMessage();
    assertEquals("a[[[1, 2, 3], [10, 20, 30]], [[1, 2, 3], [10, 20, 30]]]", result);
  }

  @Test
  void testCyclicArrays() {
    {
      var cyclicA = new Object[1];
      cyclicA[0] = cyclicA;
      assertEquals("[[...]]", MessageFormatter.arrayFormat("{}", cyclicA).getMessage());
    }
    {
      var a = new Object[2];
      a[0] = i1;
      var c = new Object[] {i3, a};
      var b = new Object[] {i2, c};
      a[1] = b;
      assertEquals("1[2, [3, [1, [...]]]]", MessageFormatter.arrayFormat("{}{}", a).getMessage());
    }
  }

  @Test
  void testArrayThrowable() {
    FormattingTuple ft;
    var t = new Throwable();
    var ia = new Object[] {i1, i2, i3, t};

    ft = MessageFormatter.arrayFormat("Value {} is smaller than {} and {}.", ia);
    assertEquals("Value 1 is smaller than 2 and 3.", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("{}{}{}", ia);
    assertEquals("123", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Value {} is smaller than {}.", ia);
    assertEquals("Value 1 is smaller than 2.", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Value {} is smaller than {}", ia);
    assertEquals("Value 1 is smaller than 2", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Val={}, {, Val={}", ia);
    assertEquals("Val=1, {, Val=2", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Val={}, \\{, Val={}", ia);
    assertEquals("Val=1, \\{, Val=2", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Val1={}, Val2={", ia);
    assertEquals("Val1=1, Val2={", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("Value {} is smaller than {} and {}.", ia);
    assertEquals("Value 1 is smaller than 2 and 3.", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("{}{}{}{}", ia);
    assertEquals("123{}", ft.getMessage());
    assertEquals(t, ft.getThrowable());

    ft = MessageFormatter.arrayFormat("1={}", new Object[] {i1}, t);
    assertEquals("1=1", ft.getMessage());
    assertEquals(t, ft.getThrowable());

  }
}
