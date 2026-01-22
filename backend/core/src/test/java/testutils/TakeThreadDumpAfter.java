/*
ACR-80a550762bff45b2bf8b4ee6f99e3362
ACR-e16a373996254169bf12ac5de8a94341
ACR-faf8134ea8d44ed786aabd78e74650fc
ACR-ea9054ff54cb4c569a284ddb86da5c68
ACR-127d9245373c4d33b50b856d4d302f12
ACR-672b17a54b03434ebbcc3a48a65d2373
ACR-f2954619e24049de916463e8e3bd90eb
ACR-bc12ff8b97244bc88bb1a303c05937ef
ACR-626cbcf9ad4e4d4aa80558a26760eb44
ACR-380415c7afb240ab86b54afa03fd50da
ACR-29504f69dbd0477c90153869de0e3e79
ACR-b9f9066de1a74b9fa268e276af9fc60d
ACR-42525f42117c4fa8b561c0dc149386e7
ACR-e8bf3412260a44b7987c8134333948aa
ACR-7d6c52ce7d4941758d2f6035ef71b8ba
ACR-94e32eb742604a5b9487593895151b1f
ACR-3b8d42634b8145d4885c4d96cd1525f8
 */
package testutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TakeThreadDumpAfter {
  /*ACR-157b4c54db094834afe13cda2647776a*/
  int seconds();

  /*ACR-3c9b6260660e48c6aac04f8ce240c274
ACR-85936a89b9ba46f78596269e53799c6c
ACR-ca75c802063e4f1c9ab1c2d94deec1a1*/
  boolean expectTimeout() default false;
}
