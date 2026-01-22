/*
ACR-afa7fc6a7e7d4ae0b35979e10982b9d8
ACR-82ee39316730445ea297c4f17d45fec7
ACR-371242d03daf4d4eb3015c62472c919e
ACR-c0ce4af86cb24a6d8b000e4e25c82ab5
ACR-bc50d9170dbf4d5687bcfa5f6259e33a
ACR-b62c8517eb734c22a9782b82d02d4f31
ACR-1a82896334424063ab22df69f1acf1f7
ACR-7fe8ff91b7444f608a8c0962c13dcd13
ACR-4d1ed5b9f6ce4220bc84019f6886dd47
ACR-cb0234d7d631400fb7bd947d4a8df342
ACR-aac5741dd0cd4cf8937870dfab5bc20d
ACR-8e5ce89d0a944070b0f54c8246603d6b
ACR-ea66c2809c614c3c862f2f0f9ebc67b0
ACR-dba0482980ad4a5aa6cb4719e9ee30bc
ACR-47a482e8c24b4daba89c80c32a54ec14
ACR-ccd6bad0f86e4e608fe49b10e4d29f47
ACR-8e01fb4932d14c5c9a32f65a8bf8d06e
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class AnalysisSchedulerConfiguration {

  private static final String NODE_EXECUTABLE_PROPERTY = "sonar.nodejs.executable";

  private final Path workDir;
  private final Map<String, String> extraProperties;
  private final Path nodeJsPath;
  private final long clientPid;
  private final Function<String, ClientModuleFileSystem> fileSystemProvider;

  private AnalysisSchedulerConfiguration(Builder builder) {
    this.workDir = builder.workDir;
    this.extraProperties = new LinkedHashMap<>(builder.extraProperties);
    this.nodeJsPath = builder.nodeJsPath;
    this.clientPid = builder.clientPid;
    this.fileSystemProvider = builder.fileSystemProvider;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Path getWorkDir() {
    return workDir;
  }

  public long getClientPid() {
    return clientPid;
  }

  public Function<String, ClientModuleFileSystem> getFileSystemProvider() {
    return fileSystemProvider;
  }

  public Map<String, String> getEffectiveSettings() {
    Map<String, String> props = new HashMap<>(extraProperties);
    if (nodeJsPath != null) {
      props.put(NODE_EXECUTABLE_PROPERTY, nodeJsPath.toString());
    }
    return props;
  }

  public static final class Builder {
    private Path workDir;
    private Map<String, String> extraProperties = Collections.emptyMap();
    private Path nodeJsPath;
    private long clientPid;
    private Function<String, ClientModuleFileSystem> fileSystemProvider = key -> null;

    private Builder() {

    }

    /*ACR-8b97ea2339d349fda4c85a549c17ec08
ACR-56fb2749ac0e479c8ac63114f194a474
     */
    public Builder setWorkDir(Path workDir) {
      this.workDir = workDir;
      return this;
    }

    /*ACR-563117b6d75a496aa5cb9472f70bdef8
ACR-aae4e07d366b49c5b7cec8d52f3d7d65
     */
    public Builder setExtraProperties(Map<String, String> extraProperties) {
      this.extraProperties = extraProperties;
      return this;
    }

    /*ACR-8bbc8a04d7fb46538477a2228d1e4988
ACR-341bfdf09be948f8b4eea8de4b045b94
     */
    public Builder setNodeJs(@Nullable Path nodeJsPath) {
      this.nodeJsPath = nodeJsPath;
      return this;
    }

    public Builder setClientPid(long clientPid) {
      this.clientPid = clientPid;
      return this;
    }

    public Builder setFileSystemProvider(Function<String, ClientModuleFileSystem> fileSystemProvider) {
      this.fileSystemProvider = fileSystemProvider;
      return this;
    }

    public AnalysisSchedulerConfiguration build() {
      return new AnalysisSchedulerConfiguration(this);
    }
  }

}
