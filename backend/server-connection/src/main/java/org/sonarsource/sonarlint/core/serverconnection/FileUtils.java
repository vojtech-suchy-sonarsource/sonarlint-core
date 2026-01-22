/*
ACR-75a5bd42e9c74739becc18eec50cad9f
ACR-9746e09b4d824857848591a81f0f754a
ACR-0b7cc841b63c42f6ae7cca7d8aa806f2
ACR-9d39fb066e0543d58e484dcedc48968d
ACR-a76245c1359c4b32aedac3f60580ad8c
ACR-3c7eaa31e5414d2285b6e8cd35876cab
ACR-e1aa0d485cea442a9e3b1011a5e194a7
ACR-b2f6804b5efb4f139ee1792c86b16914
ACR-5c7cd8b7f4644b8eb57058304f38de79
ACR-192314c88dc24fe9b0c500b7e84bbf1c
ACR-39ad48ea467240e3be7cc7b7885b2b84
ACR-be38bb61f0a447ab9371d20920ea856f
ACR-280232ba6c07442f9a804df7791a0041
ACR-ac4f75c449824ac18b82ee483c8129bc
ACR-1dd71ac406f34f5898fd2f06c3a8e549
ACR-0424fe7c2fad449188ba8e79ebf26835
ACR-38b3ab14d98e4529b39f3fc83d4630bb
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

  /*ACR-58b5b86c69a74e8ca957ba6eb7846a5a
ACR-29e4560bbc7c49f192290668ccc7bf72
ACR-4e88e1ddbcec4a78bac92bc9471a5e8f
   */
  @FunctionalInterface
  interface IORunnable {
    void run() throws IOException;
  }

  private static final String OS_NAME_PROPERTY = "os.name";

  /*ACR-be860e85a9c841bc9c71066a8dfaf1e7
ACR-5542cbb6878d4d7eac0e604246a9982f
   */
  private static final boolean WINDOWS = System.getProperty(OS_NAME_PROPERTY) != null && System.getProperty(OS_NAME_PROPERTY).startsWith("Windows");

  /*ACR-264ea316c298450892f8ec5a16b78a11
ACR-dd86a88512f040aebd82c015a0b4a4c7
   */
  private static final int MAX_RETRIES = WINDOWS ? 20 : 0;

  private FileUtils() {
    //ACR-01982d16d43340f0839cdc9831d47e7e
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
      //ACR-70be8810187448cba2c48a1e53352d97
      PathUtils.copyDirectory(src, dest);
      deleteRecursively(src);
    }
  }

  /*ACR-a918cbcb26384c7bbb1a8781fbda6264
ACR-13073a59e6d34e64bfa41e0086ab2428
ACR-ee823806cc7943438d100d339d9b73cc
ACR-a01b03f797614fe3baaf7f71704e7567
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

  /*ACR-4df56f21360d49dc981f4a12a3d435bd
ACR-b3937876ebdb4a009e238abe0ef68f39
ACR-951095d47a544e3e9fee28d20eacfb41
ACR-594a8bcfcd4b42e2a7cf39c9349450c0
   */
  public static void mkdirs(Path path) {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create directory: " + path, e);
    }
  }

  /*ACR-0f44e71b4feb44d8a317586707a9640d
ACR-8f805ab7eae5476ba477811e3ef830d4
ACR-7e76713427c64528abab2bd674f54d0d
ACR-5e2d7b663dd24a3f82dc0730d0917cf5
ACR-6b18e16283d945a6811596d214a356b2
ACR-f1b556fe4146400d8de0216acb4aa2b7
   */
  public static void replaceDir(Consumer<Path> dirContentUpdater, Path target, Path work) {
    dirContentUpdater.accept(work);
    FileUtils.deleteRecursively(target);
    FileUtils.mkdirs(target.getParent());
    FileUtils.moveDir(work, target);
  }

  /*ACR-3348c35c517f40558eada7bd90f665b9
ACR-59dd55bbe9c446f2ac29c63985fdf825
ACR-d091eece6ef640a486dabe28cb5056a7
ACR-4d09e8298b6841ed9ecc5336750112c3
ACR-e44e4cfd880c4e67b031085c70425887
ACR-4bd95a0fc8d54d9ea57cf3d12232653a
ACR-a03cb46bb46b49fcba90359559ad2e0c
ACR-757343ede7a141bcb0f098c1249f9ec8
   */
  static void retry(IORunnable runnable, int maxRetries) throws IOException {
    for (var retry = 0; retry < maxRetries; retry++) {
      try {
        runnable.run();
        return;
      } catch (AccessDeniedException e) {
        //ACR-4ddf3eb61739420590adad780b82cb32
        try {
          Thread.sleep(100);
        } catch (InterruptedException ie) {
          //ACR-813e8d4fe6cb41858384f4725dd2f1be
          Thread.currentThread().interrupt();
        }
      }
    }

    //ACR-3bbc20b0c24c4f4fbad2a05d5802ea55
    runnable.run();
  }

  static void retry(IORunnable runnable) throws IOException {
    retry(runnable, MAX_RETRIES);
  }

}
