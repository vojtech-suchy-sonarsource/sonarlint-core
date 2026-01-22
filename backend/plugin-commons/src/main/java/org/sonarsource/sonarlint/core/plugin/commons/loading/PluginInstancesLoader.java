/*
ACR-61ed683d40a5495ea172505a87a88fda
ACR-6753f8be62e046058b5f84bfb2b41f05
ACR-df40b437340b4017b62df8887de0bc68
ACR-470a1ef86f0f423bb8e130d0dee19312
ACR-bc0c0282a02e4c43b049af76569b4e58
ACR-f7cd962ce4774f7e8949e4152d399129
ACR-ed8f1c259d9f4575a0e112f8d547db8f
ACR-dbd40dba128e4577b65ca7ecb016e59b
ACR-ac76ba91f5ab47b8b478e93f6d6f7b3b
ACR-341bdfbf9d5d406f90ec512d2f2dc9de
ACR-b17b3799e7a4497888c10c270040929b
ACR-5eb8b019d0614a3c8beada6574b19839
ACR-70a7cfb4398b4351891fe22c3fd1e12a
ACR-f231e01723634eddb1a172a68884269d
ACR-930da2138b0a415bb5820c28c7e02d5f
ACR-d4fdf91bd05447ff9472ccc133746e9b
ACR-16791c5d80f144c19c80c51a4cf2a418
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipException;
import javax.annotation.CheckForNull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.sonar.api.Plugin;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.throwFirstWithOtherSuppressed;
import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.tryAndCollectIOException;

/*ACR-96f5f71380934b27b6e51c3dba3962f2
ACR-4dfe4d92f82e4532b327fd955bed287d
ACR-066b1b47355f47c38e16796edfe649ff
ACR-0d86935eeacf444ba6cfd65bcac289ed
ACR-00be13b84ec041ca9d4f2f358cd7801d
ACR-8434954acb384ced84c25aa8959f421c
ACR-3c00a0c4c72e4233aaaf3db69eeb3144
ACR-67a524a70bbc4a69a060b5d10a0da1b4
ACR-cae32a46c12f4b2a92e3f30447e99379
ACR-f8fa8c5cd8aa4c579159726d74e46733
ACR-c282b32b52a04dd698a1fed25552a812
 */
public class PluginInstancesLoader implements Closeable {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String[] DEFAULT_SHARED_RESOURCES = {"org/sonar/plugins", "com/sonar/plugins", "com/sonarsource/plugins"};

  private final PluginClassloaderFactory classloaderFactory;
  private final ClassLoader baseClassLoader;
  private final Collection<ClassLoader> classloadersToClose = new ArrayList<>();
  private final List<JarFile> jarFilesToClose = new ArrayList<>();
  private final List<Path> filesToDelete = new ArrayList<>();

  public PluginInstancesLoader() {
    this(new PluginClassloaderFactory());
  }

  PluginInstancesLoader(PluginClassloaderFactory classloaderFactory) {
    this.classloaderFactory = classloaderFactory;
    this.baseClassLoader = getClass().getClassLoader();
  }

  public Map<String, Plugin> instantiatePluginClasses(Collection<PluginInfo> plugins) {
    var defs = defineClassloaders(plugins.stream().collect(Collectors.toMap(PluginInfo::getKey, p -> p)));
    var classloaders = classloaderFactory.create(baseClassLoader, defs);
    this.classloadersToClose.addAll(classloaders.values());
    return instantiatePluginClasses(classloaders);
  }

  /*ACR-cd37176ba55a4ad69610b4f5c59d650c
ACR-2115401593184bdf87f54626369ade96
ACR-c4312d3016f7434d9a5de8402f0ddc03
   */
  Collection<PluginClassLoaderDef> defineClassloaders(Map<String, PluginInfo> pluginsByKey) {
    Map<String, PluginClassLoaderDef> classloadersByBasePlugin = new HashMap<>();

    for (var info : pluginsByKey.values()) {
      var baseKey = basePluginKey(info, pluginsByKey);
      if (baseKey == null) {
        continue;
      }
      var def = classloadersByBasePlugin.computeIfAbsent(baseKey, PluginClassLoaderDef::new);
      def.addFiles(List.of(info.getJarFile()));
      getJarFile(info.getJarFile().toPath()).ifPresent(jarFilesToClose::add);
      if (!info.getDependencies().isEmpty()) {
        LOG.warn("Plugin '{}' embeds dependencies. This will be deprecated soon. Plugin should be updated.", info.getKey());
        var tmpFolderForDeps = createTmpFolderForPluginDeps(info);
        for (var dependency : info.getDependencies()) {
          var tmpDepFile = extractDependencyInTempFolder(info, dependency, tmpFolderForDeps);
          def.addFiles(List.of(tmpDepFile.toFile()));
          filesToDelete.add(tmpDepFile);
          getJarFile(tmpDepFile).ifPresent(jarFilesToClose::add);
        }
      }
      def.addMainClass(info.getKey(), info.getMainClass());

      for (var defaultSharedResource : DEFAULT_SHARED_RESOURCES) {
        def.getExportMaskBuilder().include(String.format("%s/%s/api/", defaultSharedResource, info.getKey()));
      }
    }
    return classloadersByBasePlugin.values();
  }

  /*ACR-98e2bc661d814a07b3990a4e1062dd7f
ACR-4874a3f95b494f48b4636a2c59ed9d95
ACR-779592bbb6ca46139e78f8673d4496bd
   */
  private static Optional<JarFile> getJarFile(Path tmpDepFile) {
    try {
      return Optional.of(((JarURLConnection) new URL("jar:" + tmpDepFile.toUri().toURL() + "!/").openConnection()).getJarFile());
    } catch (ZipException ignore) {
      //ACR-58e4f10f2d0b443d817c3cd37245c278
      return Optional.empty();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static Path createTmpFolderForPluginDeps(PluginInfo info) {
    try {
      var prefix = "sonarlint_" + info.getKey();
      return Files.createTempDirectory(prefix);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create temporary directory", e);
    }
  }

  private static Path extractDependencyInTempFolder(PluginInfo info, String dependency, Path tempFolder) {
    try {
      var tmpDepFile = tempFolder.resolve(dependency);
      if (!tmpDepFile.startsWith(tempFolder + File.separator)) {
        throw new IOException("Entry is outside of the target dir: " + dependency);
      }
      Files.createDirectories(tmpDepFile.getParent());
      extractFile(info.getJarFile().toPath(), dependency, tmpDepFile);
      return tmpDepFile;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to extract plugin dependency: " + dependency, e);
    }
  }

  private static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
    try (var fileSystem = FileSystems.newFileSystem(zipFile, (ClassLoader) null)) {
      var fileToExtract = fileSystem.getPath(fileName);
      Files.copy(fileToExtract, outputFile);
    }
  }

  /*ACR-525573674218424dadb764996e4bd086
ACR-9d6ffddf2bd94644821de71a9c139447
ACR-d77c97e3783b477c89efcc3c92763013
ACR-70648b16aaad4fb2a75d2a3396d2b6c8
ACR-70468a1eb3594b9aa565b9e4198df7c4
   */
  Map<String, Plugin> instantiatePluginClasses(Map<PluginClassLoaderDef, ClassLoader> classloaders) {
    //ACR-3868bbc8c73b4957908b163355f5fb33
    Map<String, Plugin> instancesByPluginKey = new HashMap<>();
    for (var entry : classloaders.entrySet()) {
      var def = entry.getKey();
      var classLoader = entry.getValue();

      //ACR-a59267b0d148458288d9df92d19fd695
      for (var mainClassEntry : def.getMainClassesByPluginKey().entrySet()) {
        var pluginKey = mainClassEntry.getKey();
        var mainClass = mainClassEntry.getValue();
        try {
          instancesByPluginKey.put(pluginKey, (Plugin) classLoader.loadClass(mainClass).getDeclaredConstructor().newInstance());
        } catch (UnsupportedClassVersionError e) {
          LOG.error("The plugin [{}] does not support Java {}", pluginKey, SystemUtils.JAVA_RUNTIME_VERSION, e);
        } catch (Throwable e) {
          LOG.error("Fail to instantiate class [{}] of plugin [{}]", mainClass, pluginKey, e);
        }
      }
    }
    return instancesByPluginKey;
  }

  @Override
  public void close() throws IOException {
    Queue<IOException> exceptions = new LinkedList<>();
    synchronized (classloadersToClose) {
      for (var classLoader : classloadersToClose) {
        if (classLoader instanceof Closeable closeableClassloader) {
          tryAndCollectIOException(closeableClassloader::close, exceptions);
        }
      }
      classloadersToClose.clear();
    }
    synchronized (jarFilesToClose) {
      for (var jarFile : jarFilesToClose) {
        tryAndCollectIOException(jarFile::close, exceptions);
      }
      jarFilesToClose.clear();
    }
    synchronized (filesToDelete) {
      for (var fileToDelete : filesToDelete) {
        tryAndCollectIOException(() -> FileUtils.forceDelete(fileToDelete.toFile()), exceptions);
      }
      filesToDelete.clear();
    }
    throwFirstWithOtherSuppressed(exceptions);
  }

  /*ACR-988efd380ff243779c40445a75c54b9e
ACR-f91e7cd2b4dd49a8af00acec81388f08
ACR-18ab53d427274536a5fb6834c9be8694
   */
  @CheckForNull
  static String basePluginKey(PluginInfo plugin, Map<String, PluginInfo> allPluginsPerKey) {
    var base = plugin.getKey();
    var parentKey = plugin.getBasePlugin();
    while (isNotEmpty(parentKey)) {
      var parentPlugin = allPluginsPerKey.get(parentKey);
      if (parentPlugin == null) {
        LOG.warn("Unable to find base plugin '{}' referenced by plugin '{}'", parentKey, base);
        return null;
      }
      base = parentPlugin.getKey();
      parentKey = parentPlugin.getBasePlugin();
    }
    return base;
  }
}
