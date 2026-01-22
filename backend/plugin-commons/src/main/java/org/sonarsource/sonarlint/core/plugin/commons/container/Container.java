/*
ACR-2f8fe99a641a46ff93d9b1b75e393669
ACR-52b7db9e5aed48e3a468f07e5b7b6965
ACR-f7eb22bffe544cfa9ba5e8f18c51a4df
ACR-d82a944f303d4c7e9b459d8a91d222fb
ACR-7814df48b863486094bf0afa472d98ac
ACR-f82c5d45b5a24eb39379415ab5200dc1
ACR-40355a327332442e9841ffde82176c23
ACR-c4dc68b5c0ce4ef9874bf42ab8a64a79
ACR-ee36404dee844bacb35c3e16905c02c9
ACR-a49afdaeb4ad4c2fb26b13f3d4e82605
ACR-e3945878154d416fbbdf7b271d6eb08d
ACR-fe97e25785444761ace361db89389ccb
ACR-4a33ce0b752a4e2485b52b6b7a70fbdc
ACR-49dd80e68f65498cbd08943bb27d0966
ACR-00025da2f1f2413bad3e67592d76e683
ACR-b6471fdb6e4c431bb9da9e920b59034c
ACR-32e4c28ffa5849feb0e22a57ed01aa73
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.util.List;
import java.util.Optional;

public interface Container {
  Container add(Object... objects);

  <T> T getComponentByType(Class<T> type);

  <T> Optional<T> getOptionalComponentByType(Class<T> type);

  <T> List<T> getComponentsByType(Class<T> type);

  Container getParent();
}
