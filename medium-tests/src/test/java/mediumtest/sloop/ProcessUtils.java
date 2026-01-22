/*
ACR-61e6b0c3d06e4e6fa4139ad312383ecd
ACR-c55fa2fb95c343e09a359f017f738ff9
ACR-22ccec50ce5546109961573657b47178
ACR-e6dd474d012942f6ab7282aa86e375c0
ACR-108829d93ce74973a768545c96ba9697
ACR-c7a79fb3dca8429aadb7f1050079e984
ACR-6d9f6c472ddf4e6491bf8e06cf57e56e
ACR-d90d42e498d44130ba697f9c3454981b
ACR-7b94ae37d2b54d26960f34b3b84d40c5
ACR-8d9d75435c434f448d1c854122a77eb2
ACR-fbe0ef62b962497a99c88105b6cefbfa
ACR-ef4f036731ab40b7ae952c5064158c88
ACR-ad536e40b3564ff6b3307eabc63c3083
ACR-5e383adca76943fbb633d2c363ba00b2
ACR-31a7d72c6ee14e95861318a868042556
ACR-28e2d1035e554532b126a0d7f484d03a
ACR-f18f661d77b442c6854939813a0c9183
 */
package mediumtest.sloop;

import java.util.concurrent.TimeUnit;

public class ProcessUtils {

  public static int waitFor(Process process) throws InterruptedException {
    if (process.waitFor(1, TimeUnit.MINUTES)) {
      return process.exitValue();
    } else {
      process.destroyForcibly();
      return -1;
    }
  }
}
