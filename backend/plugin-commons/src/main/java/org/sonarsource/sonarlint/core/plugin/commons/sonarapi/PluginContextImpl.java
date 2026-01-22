/*
ACR-fe4da98cb3d142fe80ef3410e9497a2a
ACR-8d05654a902d4a8b82142f99de6772ca
ACR-258ad6fdacd64040ade312816f6cc940
ACR-75166ad8f7e74adcac1cf7be8e508aec
ACR-867b538294a44e8bb01c871c7b143507
ACR-d57f4ae6657a4b86ab9eda5c70a3c644
ACR-924ba54615264e46b521fb7e9a9a21ae
ACR-10ab9e97801a4ae188fcaff87d73b9bd
ACR-391db94f7bba4a88b63a5b07aa91d46e
ACR-ac5abea2502147deb617586b4da224d3
ACR-749756b621ae4acd98f155d360f453e8
ACR-bef1a220664347209528eb93f0c4eacc
ACR-00b7a7a676704216b7a26cf98c7359b5
ACR-c7a0810f3f1d409f81e9b568c3f330a0
ACR-fb3a62916c53422ab02bd91817b10e11
ACR-1194a550b18f441a8d813dbf6b208221
ACR-fd49ad07aa8047c0aa771c752a0ae8a9
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import java.util.Objects;
import org.sonar.api.Plugin;
import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

public class PluginContextImpl extends Plugin.Context {

  private final Configuration bootConfiguration;

  private PluginContextImpl(Builder builder) {
    super(builder.sonarRuntime);
    this.bootConfiguration = builder.bootConfiguration;
  }

  @Override
  public Configuration getBootConfiguration() {
    return bootConfiguration;
  }

  @Override
  public SonarLintRuntime getRuntime() {
    return (SonarLintRuntime) super.getRuntime();
  }

  public static class Builder {
    private SonarLintRuntime sonarRuntime;
    private Configuration bootConfiguration;

    /*ACR-28cbd395f79c430b8a8a432de6359407
ACR-e62c4f9db8f04f64bd25a918530e6e4a
ACR-b771feca8c214040be4c8988586d1c14
ACR-79f963da29f6402c980640eae958b038
     */
    public Builder setSonarRuntime(SonarLintRuntime r) {
      this.sonarRuntime = r;
      return this;
    }

    /*ACR-706b27eed4084a2db83dee01ae622a41
ACR-11cd3f9d35224b44810176de34ea1f5e
ACR-41ee8a05f24c47efbe7896b5719edab2
     */
    public Builder setBootConfiguration(Configuration c) {
      this.bootConfiguration = c;
      return this;
    }

    public Plugin.Context build() {
      Objects.requireNonNull(bootConfiguration, "bootConfiguration is mandatory");
      return new PluginContextImpl(this);
    }
  }
}
