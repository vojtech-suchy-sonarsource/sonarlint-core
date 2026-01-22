/*
ACR-54a49c7ec97a43e196fbde3ebcd8ec07
ACR-090a008a0034457d881d0844ab9fcb1c
ACR-00175230579044b290e1b7e537b8cca3
ACR-97be77681e3b490e9cf502c40792c1f9
ACR-2a75c7a1129f4db6a318abeb2bf044cb
ACR-1719f9a3e7db49c29ec6ff4d0991910b
ACR-2117b91d90f34033b5ea054312b311f0
ACR-0c9570a7e4494ab88b94546b0442a420
ACR-9e0489bffe0c4f049c3a910661f7ad74
ACR-d01867be3db14884925e7c287b4ec703
ACR-0cd827b1153b4ccba5362485e22adcc7
ACR-08aa4ffec498422aa07c059b33788dd0
ACR-c30b9d6f3e1b4a919e8852093b906d7f
ACR-2d9f0bba1d214202b1a22e3646e69bfb
ACR-4f030650e03d4a3f9824ac8d9db29f14
ACR-aa4c347a13174ef18cffab2359074516
ACR-9876b2da6eb74651a0e274999fd2b852
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.Organization;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class OrganizationStorage {
  public static final String ORGANIZATION_PB = "organization.pb";
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public OrganizationStorage(Path rootPath) {
    this.storageFilePath = rootPath.resolve(ORGANIZATION_PB);
  }

  public void store(Organization organization) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var settingsToStore = adapt(organization);
    LOG.debug("Storing organization settings in {}", storageFilePath);
    rwLock.write(() -> writeToFile(settingsToStore, storageFilePath));
    LOG.debug("Stored organization settings");
  }

  public Optional<Organization> read() {
    return rwLock.read(() -> Files.exists(storageFilePath) ? Optional.of(adapt(ProtobufFileUtil.readFile(storageFilePath, Sonarlint.Organization.parser())))
      : Optional.empty());
  }

  private static Sonarlint.Organization adapt(Organization organization) {
    return Sonarlint.Organization.newBuilder().setId(organization.id()).setUuidV4(organization.uuidV4().toString()).build();
  }

  private static Organization adapt(Sonarlint.Organization organization) {
    return new Organization(organization.getId(), UUID.fromString(organization.getUuidV4()));
  }
}
