/*
ACR-908fd1a627584080a836dbca5c4104c0
ACR-e05fe04d19404d3ca05f211e3698a880
ACR-d753dd438ba4476eab35f8fa6f484f03
ACR-8398a8aa82e248d6a7ba0890625360fe
ACR-defb63a45f624044b65d97d0100109d0
ACR-86b5edf42bc44f6b823d868214e025bb
ACR-5f3171cb5ddf4d3ab0ab27c679f2855f
ACR-735ff449e39948c9962fa80a6d8a89a3
ACR-2ac170a958b44d7eadada53027078e49
ACR-5913b29f1e6c4c01a221b18c5b334801
ACR-77878f14c4f143fe9d9e826726eadac6
ACR-0628df7edff6480aa5cd7a1b270400aa
ACR-3037833ddd964681ba31a4ae88678180
ACR-62e09b5560934545bf75bd39b6b01576
ACR-6d8f28289bbd4c04b0a53e054042a7a9
ACR-70294cc74ee942358a37e79aa1e81322
ACR-aadb18e08df1464f87be9716136166f3
 */
package org.sonarsource.sonarlint.core.commons.storage.local;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateTimeAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.OffsetDateTimeAdapter;

public class FileStorageManager<T extends LocalStorage> {

  public static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Path path;
  private final Gson gson;
  private final Class<T> localStorageType;
  private final Supplier<T> defaultSupplier;
  private T inMemoryStorage;
  private FileTime lastModified;

  public FileStorageManager(Path path, Supplier<T> defaultSupplier, Class<T> localStorageType) {
    this.path = path;
    this.gson = new GsonBuilder()
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter().nullSafe())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
      .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
      .create();
    this.localStorageType = localStorageType;
    this.defaultSupplier = defaultSupplier;
    this.inMemoryStorage = defaultSupplier.get();
  }

  public T getStorage() {
    if (!path.toFile().exists()) {
      inMemoryStorage = defaultSupplier.get();
      invalidateCache();
    } else if (isCacheInvalid()) {
      refreshInMemoryStorage();
    }
    return inMemoryStorage;
  }

  public boolean isCacheInvalid() {
    try {
      return lastModified == null || !lastModified.equals(Files.getLastModifiedTime(path));
    } catch (IOException e) {
      LOG.warn("Error checking if cache is invalid", e);
      return true;
    }
  }

  public void invalidateCache() {
    lastModified = null;
  }

  public synchronized void refreshInMemoryStorage() {
    try {
      if (isCacheInvalid()) {
        inMemoryStorage = read();
        updateLastModified();
      }
    } catch (Exception e) {
      LOG.warn("Error loading data from the file", e);
    }
  }

  public void updateLastModified() throws IOException {
    lastModified = Files.getLastModifiedTime(path);
  }

  public T read() throws IOException {
    try (var fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
      return read(fileChannel);
    }
  }

  public void tryUpdateAtomically(Consumer<T> updater) {
    try {
      updateAtomically(updater);
    } catch (Exception e) {
      invalidateCache();
      LOG.warn("Error updating data in the file", e);
    }
  }

  private synchronized void updateAtomically(Consumer<T> updater) throws IOException {
    Files.createDirectories(path.getParent());
    try (var fileChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
         var ignored = fileChannel.lock()) {
      var storageData = read(fileChannel);
      updater.accept(storageData);
      storageData.validateAndMigrate();
      writeAtomically(fileChannel, storageData);
      inMemoryStorage = storageData;
    }
    updateLastModified();
  }

  private T read(FileChannel fileChannel) {
    try {
      if (fileChannel.size() == 0) {
        return defaultSupplier.get();
      }
      final var buf = ByteBuffer.allocate((int) fileChannel.size());
      fileChannel.read(buf);
      var decoded = Base64.getDecoder().decode(buf.array());
      var oldJson = new String(decoded, StandardCharsets.UTF_8);
      var localStorage = gson.fromJson(oldJson, localStorageType);
      localStorage.validateAndMigrate();

      return localStorage;
    } catch (Exception e) {
      LOG.warn("Error reading data from file", e);
      return defaultSupplier.get();
    }
  }

  private void writeAtomically(FileChannel fileChannel, T newData) throws IOException {
    fileChannel.truncate(0);

    var newJson = gson.toJson(newData);
    var encoded = Base64.getEncoder().encode(newJson.getBytes(StandardCharsets.UTF_8));

    fileChannel.write(ByteBuffer.wrap(encoded));
  }
}
