/*
ACR-c40f7899064b40358157d622f34a1254
ACR-26f57ab7a21e45b690dd98a11444ccef
ACR-a954399fcb0f446da751ede038e487b3
ACR-f8d98c1fa36a4ac4a33a3aacda87bd92
ACR-3c77a57c1ea04d00aff4287b34109da8
ACR-d9116dc4e0b744d1a986c34e79f9eaf9
ACR-22ffc5158de6405789f0fb5c39ca8aac
ACR-3c44ec96cd804252bbe3d875e98d6a95
ACR-233d8fe09212434ab661be50e19a1502
ACR-f4355641133c4860b4289ceda69c0bf4
ACR-42fe888b37d14e4ba143dda789eb23dd
ACR-084737c0a8074c3eb4f1a15301c9a048
ACR-df19764c0dde499c9398767d03f72f56
ACR-fafbf234d22241e281c07e2913a10d40
ACR-a56f9dba40a74db0b551fc42b66e1adf
ACR-ddd682242ddc4f9ba76ac2d99207ca6d
ACR-7c2bf769f7214137b89a7c202d491907
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

    /*ACR-0167936a61544cd4bf387c9f88a7849c
ACR-56ed78529c6248519ceac65b253d02c9
ACR-fc7cc2ca1ca644f992706a83ecb34868
ACR-c8337da8e1734068b1cc988dad5bc808
     */
    public Builder setSonarRuntime(SonarLintRuntime r) {
      this.sonarRuntime = r;
      return this;
    }

    /*ACR-8a8530ca3deb4b4b938f96e82e86bb16
ACR-4f777a73322d45fbb7a346936aa9814a
ACR-5e7f6c3c92504e59852e25bc47c9e6e7
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
