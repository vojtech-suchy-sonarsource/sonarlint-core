/*
ACR-5e9ce689e06140ac9d6a2c969ebcf678
ACR-e53f7099265d49e2a3c8073f30281efd
ACR-0ca23426a22d40fa8c98cffc6896a6c4
ACR-83597a8a7d3c4989877ba44212772cc4
ACR-bfb90ed1422146bd871990b050743896
ACR-ffefed0c3eb54cac9e02561d4b98e9ce
ACR-64d8928a533a4725ae150b6bef114624
ACR-ddf8cf90860747b08597cd14b218e504
ACR-518dac4686b44a12b912554e5287ab9f
ACR-129c384a5ef74d6d974443039d890ee4
ACR-0ef2bd18542f4020a9335f7b8447019f
ACR-0085084ca7a44af88230f94541447a19
ACR-b3da18c92e3845968c52964902f25eea
ACR-f9ad96a1c487434faaecbd1e4dc8b029
ACR-6d58cc36554f43bbbc76c37b72f80105
ACR-0369bcf9ac7b4bfea586cb977587895f
ACR-15b6f620ab1e4c428d7e47e8e302a6b3
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

/*ACR-85f42fe9328c4d11bb585e096cbeef67
ACR-a919702cedad41338d55d23c83795919
ACR-16c21f7473b5411bb738567de90921f6
ACR-74f120722eae4cbb8b3cc4c659f0241b
ACR-f32af180a11b4236b5d764a731257176
ACR-8df39066e1874dd5a56aa0146838a4aa
ACR-709c722f5bac42e682dd677cff7b7425
ACR-dc2045f69b6f4d149232d48ea7a7d93f
ACR-e1ed8e7581a94d31b176db6da523be15
ACR-e81b818bc57741b8970c43d417068db9
ACR-8010e41fade14a298c84181714aadf7f
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

  /*ACR-4a00ff7f72184bc4906c693685d3b0dd
ACR-0b4970d610b84033904b286db6ea01b7
ACR-460fcab9db4745a49c24b4592e529e6f
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

  /*ACR-859c2253553e4772bca982225347f29c
ACR-af75c4aa08a04e40b629ebd843993959
ACR-857c2fb694374661931bb14aef4e3e5a
   */
  private static Optional<JarFile> getJarFile(Path tmpDepFile) {
    try {
      return Optional.of(((JarURLConnection) new URL("jar:" + tmpDepFile.toUri().toURL() + "!/").openConnection()).getJarFile());
    } catch (ZipException ignore) {
      //ACR-d06dcd555782453384b9cfc4c1b95aed
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

  /*ACR-28b477d7e24b453b9d0565cf7cf3e05d
ACR-a60d0419a6444d668007da975065a20d
ACR-3f76295dddaa4ce6ae73aea1f3d978ff
ACR-a2e9dffc0c164c7da30dbc24e42716c1
ACR-755b6754d5a1473fa7c61068e2a58fc4
   */
  Map<String, Plugin> instantiatePluginClasses(Map<PluginClassLoaderDef, ClassLoader> classloaders) {
    //ACR-a50b2e47afc7444e952ce8957200472a
    Map<String, Plugin> instancesByPluginKey = new HashMap<>();
    for (var entry : classloaders.entrySet()) {
      var def = entry.getKey();
      var classLoader = entry.getValue();

      //ACR-1fd4285793124bd7a3b679e996508e7b
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

  /*ACR-ba5d7ccdde4b4db499ae9ee0c257ee83
ACR-296a370653084c509802973c901d8c2d
ACR-efe6748bf84e4dd1a98e412b88173308
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
