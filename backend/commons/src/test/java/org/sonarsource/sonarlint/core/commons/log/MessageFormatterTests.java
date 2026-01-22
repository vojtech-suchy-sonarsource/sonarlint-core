/*
ACR-464a8dc8453b413d9d20f5319ab744ac
ACR-034c6d8fc85547d093999516c8a4386a
ACR-6953ffc519b044d8a05391f4543bf443
ACR-4acfe2a41fc04ed0bbcd1f60c34b6369
ACR-eb821be880c840148fae65119fc39f0e
ACR-581b0e90d24c4d69a6352656d20bca19
ACR-ca2cc7a98add46eba2f2ef2d512e63f7
ACR-c4b909332f0e46bb81ef0f86f28aef87
ACR-67949db83242473bb2d4d624a7e6efd3
ACR-b9da99a2c4ff4896ba4b643c76535d73
ACR-c9cdf6a5b896483eaf786cc01f0ac092
ACR-b40b6d314680405bbf83df71c09a43d1
ACR-b3fb82245d964bc491f11ae21b473ef6
ACR-b91ae3256d0d4a8cbb4ba2b7016859bb
ACR-19cf57dee77c48afb2f3d4237a99eab6
ACR-927e63a1820641e4a48d15c3ba29a858
ACR-059428de0c7b4ae98c2eba3dc4793e13
 */
package org.sonarsource.sonarlint.core.commons.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/*ACR-a5c5f7a5c2e84f00b54f09d083fd0b49
ACR-36744d4f71d44cf6bb2d594bcac7c70d
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

    //ACR-5f0bc508b3cb487486a370e7d45eb64a
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

  //ACR-cefa458bd1a64317bb5860056b81e53a
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

    //ACR-783b9d9a8ac648578012c961ba9280f1
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", p1}).getMessage();
    assertEquals("a[2, 3]", result);

    //ACR-272cbecccbc64f4d8b7e515bce6b8a01
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new byte[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-ba36239554d144f69935488936d5d59d
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new int[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-e91e1adae48b4ace93f698746e903ca4
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new float[] {1, 2}}).getMessage();
    assertEquals("a[1.0, 2.0]", result);

    //ACR-b8a5282c0b354b68aa79063758abcd77
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new double[] {1, 2}}).getMessage();
    assertEquals("a[1.0, 2.0]", result);

    //ACR-d2726fd5e74548c2889247ce83268d78
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new boolean[] {true, false}}).getMessage();
    assertEquals("a[true, false]", result);

    //ACR-beda9913541a4c8b82dd508c2b7dfa65
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new short[] {1, 2}}).getMessage();
    assertEquals("a[1, 2]", result);

    //ACR-a9310fc500d843059b97732d23cdd934
    result = MessageFormatter.arrayFormat("{}{}", new Object[] {"a", new char[] {'a', 'b'}}).getMessage();
    assertEquals("a[a, b]", result);

    //ACR-a541a44e066046ecb60c88213c6ff403
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
