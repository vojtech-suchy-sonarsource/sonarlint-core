/*
ACR-2a2121c66e974796b37808700f457731
ACR-ab961a6da47445e586d3af5a3c742efb
ACR-f15b1f539f45406f85351a920f6268f5
ACR-64d8bcc45a8146ed97139f700fc5d17a
ACR-e8dae3c33d5441ea8eeff88248bd1261
ACR-6eea1eccfd2e4534b16f8bb964ce2042
ACR-a4c76f7fb73842699927aeeb0b6a637a
ACR-6093f94d3ecd43059f852913d02f5725
ACR-ce60f80a08be409abeaf93bc9543569f
ACR-f743b18711a4461b840f35d9ed016099
ACR-072d05f860ac4917a6427cdff7bd7830
ACR-45d68cf25f3e400292d714b1ddeab8d1
ACR-0bc27467d8d9427cacc0acfd58602fce
ACR-aaf42d7271f143088440e81d136685d2
ACR-af519e3df44c454da15dc0eaace4bba3
ACR-dab85272ff0742ab890f71c9ac793524
ACR-cf965793dd1b46d2a30ad014b02c6e71
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import org.sonar.api.SonarEdition;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.utils.Version;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

import static java.util.Objects.requireNonNull;

public class SonarLintRuntimeImpl implements SonarLintRuntime {

  private final Version sonarPluginApiVersion;
  private final Version sonarLintPluginApiVersion;
  private final long clientPid;

  public SonarLintRuntimeImpl(Version sonarPluginApiVersion, Version sonarLintPluginApiVersion, long clientPid) {
    this.clientPid = clientPid;
    this.sonarPluginApiVersion = requireNonNull(sonarPluginApiVersion);
    this.sonarLintPluginApiVersion = sonarLintPluginApiVersion;
  }

  @Override
  public Version getApiVersion() {
    return sonarPluginApiVersion;
  }

  @Override
  public Version getSonarLintPluginApiVersion() {
    return sonarLintPluginApiVersion;
  }

  @Override
  public SonarProduct getProduct() {
    return SonarProduct.SONARLINT;
  }

  @Override
  public SonarQubeSide getSonarQubeSide() {
    throw new UnsupportedOperationException("Can only be called in SonarQube");
  }

  @Override
  public SonarEdition getEdition() {
    throw new UnsupportedOperationException("Can only be called in SonarQube");
  }

  @Override
  public long getClientPid() {
    return clientPid;
  }

}
