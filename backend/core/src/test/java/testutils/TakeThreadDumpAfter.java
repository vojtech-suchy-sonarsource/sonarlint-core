/*
ACR-e5eaaf99364e42c1881c138acbcc8347
ACR-9b517537c8fb40f08d3b96ff2cc19cfb
ACR-3cd598ef06c843c0b10f9d622e2b619c
ACR-d2d00f0606f34aa8b5abe9b9498ff77a
ACR-95c9151418f84b26ab516d5fce522b82
ACR-75c57b7625b948b9b2ded8a2975d97a9
ACR-c57815aadc604dad85c3ee8c7456ac71
ACR-53e56b017cae49d7904d68b34b20717b
ACR-c9a10e8e69ef49fa928ab314a3266f3e
ACR-1bc0edcbf14143eb817f8b1629792a29
ACR-f1e7358cdbaf4e4c95cd555d0ce07ac8
ACR-208cdb62154a42d997af3e12da8e4967
ACR-e7597a81a2da48b294368e3dbf72f4b5
ACR-34f0dbf7ed87420b845c62fb788021ec
ACR-cebf1f3712dc41a68c8efa1f9502d9c2
ACR-2fe5d276f87246c3bf7c9deaf1e25402
ACR-70d5b3e4a04a46b9830a8aa40d6589c4
 */
package testutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TakeThreadDumpAfter {
  /*ACR-e5e50445df7b40ddbb9edd1c647b456d*/
  int seconds();

  /*ACR-331444af95d5425baefd1b73ab5cade3
ACR-249da21a6585447a87da35fa2ccf785b
ACR-c0ad5cc636574f24846c53d4cf3a36d5*/
  boolean expectTimeout() default false;
}
