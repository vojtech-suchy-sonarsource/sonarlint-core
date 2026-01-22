/*
ACR-e6bc9f2ce22e4fda8c0a2bbeb8e71726
ACR-ecad2a6b06584891990cfeb1f8190f38
ACR-61ffa19c330049bea65d6a76f7cf6f3f
ACR-ffc4e289c42843bab95d47a7f4c95684
ACR-c78015b3e0c7491e8f6143bf2afc27e9
ACR-b90ff6c61fd94b828045a7dd3eb13b71
ACR-0b930b79f5b54e76a67188fc272f2e8c
ACR-c10b1800d3274c88b93f94b723bc3ce0
ACR-2eda6baf96ef4df7a8aea0b702e26754
ACR-a927082bd3744d52a6e6d3460733af13
ACR-03368c4105a149989b9096b37703c098
ACR-c5509556576b4c518c13f7121f085831
ACR-7dfb8f7ee3324f68941471dbf5c4b77d
ACR-1f996176ff2242e2893dfce7a9d3ef25
ACR-809def4b856f4dbbb1f7d8e5fdd017a8
ACR-9b48b48605dd4b30be71385a4c432fc1
ACR-f64ce76522734b93973480ec2ed4b7dd
 */
package org.sonarsource.sonarlint.core.serverapi.exception;

import org.sonarsource.sonarlint.core.commons.SonarLintException;

public class ServerRequestException extends SonarLintException {
  public ServerRequestException(String message) {
    super(message);
  }

  public ServerRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
