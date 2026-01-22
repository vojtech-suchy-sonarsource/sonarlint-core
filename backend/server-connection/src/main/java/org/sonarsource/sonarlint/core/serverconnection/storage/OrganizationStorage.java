/*
ACR-599b745de23b48ae8b752ba032668899
ACR-a99497e8cd134ec1942b425818c6e252
ACR-8bf490e056a84310868b3b330a6bec34
ACR-80646d885fec42e9815b7f8bd8dab5b4
ACR-b060ebf373fb426696be1b6b3e180edb
ACR-3044ad88e7884d2290e4ab363e2480dc
ACR-9a1065c87b0140c59adfe7d0e85813b3
ACR-2d2b2db1f68c49b485517e0a89a72836
ACR-5ac2c9de6f2345a3bcebd7e33e8a326d
ACR-69bd0bff539c4752bc223717ded958bd
ACR-04472c13abad4021bc11a7dc65aaf151
ACR-7a6fc539aa634b72a13dec209b9760bb
ACR-049bf03a20174532a8be9ad156cddf76
ACR-f11dac04f1fb49b98563ab6d984333fa
ACR-e17bbe54166c4dbda5580f2da09754a4
ACR-26084edd7b284201ad1fdb2568d09295
ACR-b6164b0268964c79a199596e3fc8af16
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
