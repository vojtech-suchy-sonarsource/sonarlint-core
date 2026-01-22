/*
ACR-5dbcd094d3ca48ab97ccf4751fe41365
ACR-8537283a4ccb496e8b5d7243c3ce06af
ACR-253b3ae5c70a4548aee2ecceab652e83
ACR-8f2adca14be2419b995514fa6e49e945
ACR-2731d2d296894fcc9486ef3fb68c57a7
ACR-764838f2c71148c19b60b86fb90023c6
ACR-535a6b3491f549e7ab669696c8424223
ACR-7067fe4627e94132b59f6ba4a578833f
ACR-090984be58e04f2e9c5551d27de435cc
ACR-7eefdc82b0c2428988ffcf80038a89b1
ACR-54cb2c50ab744c818bda7557c88b1e25
ACR-7aa102fc276d419dbcbfbeac558519f0
ACR-1459e01aef1941bd85935d947cd080bc
ACR-3fbfce01ec454f9cb434696f1af63793
ACR-098f6d4d20b34e55a175dda8da7ca777
ACR-6b550f1c538448759df09ec525785f22
ACR-872b6ab629794c37a96fc3620319ddcf
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
