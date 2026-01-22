/*
ACR-108bf2b2eabb4686a28dc81ccdec49f4
ACR-4deb4a1d80c24539a726fe7da1704348
ACR-8961b693682b429e99abfc8e96442de1
ACR-6da82c93adbd4274aab01d84b4e21d1e
ACR-6852be61848644e6a3c9518481c17c5a
ACR-2f2f0f3e1c2e4d7e9b7bf9960b2e3432
ACR-2eb541bbd8bd4fb584be023fe028623e
ACR-719f829c38994918a69f376639315165
ACR-796d7b08eae34bcb970f00ab31985537
ACR-4642c17a5098431f8fe622e8e10289ce
ACR-2271e62dbf2d4e06bd6f7571116e94f4
ACR-d1a878a1e25d4c7e92dc39461d8b988a
ACR-9d29ffbd44ca417da09eccf7518873ac
ACR-1841fc3c8363478592077bbf33d72a2c
ACR-8aa2370398d949d884650637dfd6d149
ACR-f22ed8e30e034ff9a476d397502bca0f
ACR-9f023eb749e0456e9f68ea79e1d0cd08
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
