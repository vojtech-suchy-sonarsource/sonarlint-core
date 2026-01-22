/*
ACR-9da7429c0fc844298ed2a9ea30b0f292
ACR-a01e897fd0e848729a772e67dc28a55c
ACR-6e0d4fc743f44ca4a7e9b24b7d825290
ACR-85ae7c150d7641d2a121703efa4d477b
ACR-d3c39eb5bdc94f6a93de8e7379edce42
ACR-09db933656474f30b4fb6bb8a328aec3
ACR-3700fddf820e457faa88bb16fb570a7c
ACR-d83f1b7d427944a1994c67a47141a213
ACR-2b0ee62a6bed4005897c436ae58a4a7b
ACR-6bae7520d1c6475097a9f40346c8d993
ACR-b58354ed694048ebb46e3c5110e8e0bd
ACR-fa38b25309134b61af2be12f212fa424
ACR-2df581e39b40451dbd5c77c82daf9e4c
ACR-47ebf42a21bb47188d996109aea8b5ba
ACR-f02bd50b8932467892c3c075969d7211
ACR-34c0481bbf024dd087dc044e16b25f0a
ACR-f193a32f93b54df0a8d4001baa6d1d24
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

    /*ACR-7e1947cb96504a88b83388829eb6f1a0
ACR-9676e9ce5b2b4f8bac42915c54c897eb
     */
    public Builder setWorkDir(Path workDir) {
      this.workDir = workDir;
      return this;
    }

    /*ACR-5fdfd66dc5ba4a03813edfe72a90ee12
ACR-3bf8ba1cb91d40a2a684993017ea8845
     */
    public Builder setExtraProperties(Map<String, String> extraProperties) {
      this.extraProperties = extraProperties;
      return this;
    }

    /*ACR-519a82ddbcac47cb8e7bfa714ed7dea4
ACR-54845a5b62e54075b29428a7da4e09a0
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
