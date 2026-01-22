/*
ACR-d585ed7fa1384d5e98ad0d1dc56cbb5c
ACR-7c99c8a476e34f8cb24feaaa789ec2e9
ACR-bca4a5ec157d423c9542e28aae9e3cf0
ACR-cbd604fd92a74b4ebe0d980418209e6b
ACR-b509a621ffc84770946a7720025ffeda
ACR-0920c48ad0824338b256ca42b9d3562f
ACR-4e15febd94da46fa8794b08e454e6530
ACR-686529032afd4c7dadd589252d15c8a8
ACR-b33a42d4a9204b98ae52520831bb3797
ACR-70619f794286446186ee880a86e44e59
ACR-0f55ac655fc3456e9b0fdb2edf61f337
ACR-e6e36c6822fa4d5f955ba5f6fc7bc4cb
ACR-cae77df4d276428092120aa67ce21208
ACR-b866f33153364482a6ef2a4b38d1d63d
ACR-734d20c8a0d64821b2dd355a2971bb39
ACR-91397004ee4d4b0c8a46f380dc7fd57b
ACR-a4b74b6dec174a4fb465778c31494138
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import org.apache.commons.io.file.PathUtils;

public class FileUtils {

  /*ACR-8a85768c177b468a9c06d3d02291d96e
ACR-c0812295777c490bbb61d04a5ed58888
ACR-4e1c373286814556af9101211eb5f807
   */
  @FunctionalInterface
  interface IORunnable {
    void run() throws IOException;
  }

  private static final String OS_NAME_PROPERTY = "os.name";

  /*ACR-e813c7451aef443791aeb891769b9ae5
ACR-dc33fd36bc4e41c489817bf24947c5b9
   */
  private static final boolean WINDOWS = System.getProperty(OS_NAME_PROPERTY) != null && System.getProperty(OS_NAME_PROPERTY).startsWith("Windows");

  /*ACR-c8f7ff5d04674c5aa81eaa059f84b93f
ACR-84e6b1dc9a344f29a64f3278b38f4cc8
   */
  private static final int MAX_RETRIES = WINDOWS ? 20 : 0;

  private FileUtils() {
    //ACR-27077781028b4211926e46c9c4b46ac1
  }

  public static void moveDir(Path src, Path dest) {
    try {
      moveDirPreferAtomic(src, dest);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to move " + src + " to " + dest, e);
    }
  }

  private static void moveDirPreferAtomic(Path src, Path dest) throws IOException {
    try {
      retry(() -> Files.move(src, dest, StandardCopyOption.ATOMIC_MOVE));
    } catch (AtomicMoveNotSupportedException e) {
      //ACR-20755c1e94df472989cbacee6cac3e7f
      PathUtils.copyDirectory(src, dest);
      deleteRecursively(src);
    }
  }

  /*ACR-21c94711ac164e2e8e8cf57f65e8598d
ACR-a86160f8356b4d8cb2b5189ef0e9269d
ACR-68ef1e80e2aa4ea4bed6c5d57e45aa76
ACR-cb7810bed1bd497eb7a89d5a7b88d594
   */
  public static void deleteRecursively(Path path) {
    if (!path.toFile().exists()) {
      return;
    }
    try {
      PathUtils.deleteDirectory(path);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to delete directory " + path, e);
    }
  }

  /*ACR-f79977593a454555b961079d48f253a6
ACR-7af32a4af7d5421cbca873efa73d6cf1
ACR-b24627109a7e4619b183c445275d76a4
ACR-79f0779b99324409b17935603fc9eb38
   */
  public static void mkdirs(Path path) {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create directory: " + path, e);
    }
  }

  /*ACR-a8f88378fe05406eb49e6d16290a44f4
ACR-3a37b198bd97430d8f0bc6228d061f8c
ACR-f4b000313a434a5aae6a5331a9bd5dd1
ACR-e882eb8611b3471fb13f32f8080c8a6a
ACR-82cd9127ee624224ac0735b2cc4b1926
ACR-01edf3a1422d42febb93baa35b47dec6
   */
  public static void replaceDir(Consumer<Path> dirContentUpdater, Path target, Path work) {
    dirContentUpdater.accept(work);
    FileUtils.deleteRecursively(target);
    FileUtils.mkdirs(target.getParent());
    FileUtils.moveDir(work, target);
  }

  /*ACR-87af4c7d42054d4495753bb21cddfd4e
ACR-eb6693161c8b4629a6b8645a02b00524
ACR-810c657d5c424bb1bfb0cedd27adfb9c
ACR-3310f860698c4519944360efe7cf43a6
ACR-51c5fcd913ca4321b28c5c16c5c54bf7
ACR-f908941da5f243c3a7c387dee67bd069
ACR-5688fa96d3fc476387372b6343cfd198
ACR-2790b5fe1c194a22a8c66b78d5872766
   */
  static void retry(IORunnable runnable, int maxRetries) throws IOException {
    for (var retry = 0; retry < maxRetries; retry++) {
      try {
        runnable.run();
        return;
      } catch (AccessDeniedException e) {
        //ACR-7720691c60064bac96e049e102a383d1
        try {
          Thread.sleep(100);
        } catch (InterruptedException ie) {
          //ACR-1c1b3d3736ae434fb6732a7cb11b2eb3
          Thread.currentThread().interrupt();
        }
      }
    }

    //ACR-2b693c43b98e4743be730ca5c55c7f87
    runnable.run();
  }

  static void retry(IORunnable runnable) throws IOException {
    retry(runnable, MAX_RETRIES);
  }

}
