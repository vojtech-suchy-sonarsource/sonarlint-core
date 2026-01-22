/*
ACR-97ddab6ebb3b4e018392fd43aab63fcd
ACR-86a4f68a32e8411ebf10d7965faecf1f
ACR-46f7a9a246e24e3d94cfe473175a33e3
ACR-d6ec214ef7954645a0da18010966382b
ACR-14e3d8b0d67d43e3a3d12290aa75bea9
ACR-1d7e23958dce43d896485e15b2c988ee
ACR-d99ad75bab46450aaa0ed9515f88545e
ACR-56bf9565a0064674b6834ae7496cfa3e
ACR-0a1d37c5564e46da81aa19d43969a821
ACR-63cf3b208d434810bdcf66a6686510f0
ACR-03f469772a904b2091185149e2ef9bca
ACR-de418649fb5745c8ba6479d7bbb586b3
ACR-7c2943642a0a4d399b2326c74d2bb159
ACR-2dc165489e83459793e79f9b7d342b60
ACR-9870386cac8c4f5481e8b7b6dfb651b4
ACR-c729ebbf9f754717be2ec3d70ec70eda
ACR-368ea0c358704c40bc73430db31ac7f2
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

/*ACR-6adbe2a7212b47cf9153917b860c38a8
ACR-4f481db225a944949413cebfa1f76207
 */
public interface OptimizedFilePredicate extends FilePredicate, Comparable<OptimizedFilePredicate> {

  /*ACR-6e5d9a8563774193b01d508b87f12d45
ACR-c2c67c7495564113beda80906cc3ac7e
   */
  Iterable<InputFile> filter(Iterable<InputFile> inputFiles);

  /*ACR-0e2209d7bb2d49089fac43adbfd15ea4
ACR-82f5d15b8be94fb585768d02255aaee1
   */
  Iterable<InputFile> get(FileSystem.Index index);

  /*ACR-c1ab4b3c501d4e4683e161d5b1ee4ff0
ACR-aebf82559f6b474f881f2a31d9312dd5
ACR-3e8aa22d03294b5d81343563da766116
ACR-15cf179666434edc94af5c06d0400700
ACR-0cd32c5e68474cacb2fb858d57aeff28
ACR-1f8ad69c7cc84be689936e23c0d59aee
   */
  int priority();
}
