/*
ACR-bb2d69d45ef2431b9f4448c55c8017b1
ACR-ee209c9db289407ca2b21c8568628fe8
ACR-390ad774e96a43379835badd9d375b22
ACR-ea10076f9d554e70bff0ed8b822fe3cc
ACR-881da3b171b441ff9fd76d95aab634fd
ACR-d0d7c0ba91c24e469175e8f64d0cf774
ACR-001ac7a933b34cef86f624f4fcd3bf51
ACR-86ade73dd8504052a138d5414d493ab0
ACR-397222bcff5d488b996fde6a96795833
ACR-1d301048fef448ac9119334ed3fcd35b
ACR-8b8bc0c13e2f4831baf215d4663ae7b2
ACR-9fc34a07dd5b4bb68cf472f9e9de90c5
ACR-3a50ef8d2c2446d2833a2b0be7d0a3e4
ACR-343a3d081f5a436b97a984a8da21f89c
ACR-67a16223bb5e4e45afd1a34e0ebfe8f0
ACR-c9605f06b5884f6fa61b7dfab0cc0ded
ACR-aba4e3f2f99b40e2bde32360f5c34286
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.error.NewAnalysisError;
import org.sonar.api.batch.sensor.internal.SensorStorage;

import static java.util.Objects.requireNonNull;
import static org.sonar.api.utils.Preconditions.checkArgument;
import static org.sonar.api.utils.Preconditions.checkState;

public class DefaultAnalysisError extends DefaultStorable implements NewAnalysisError, AnalysisError {
  private InputFile inputFile;
  private String message;
  private TextPointer location;

  public DefaultAnalysisError() {
    super(null);
  }

  public DefaultAnalysisError(SensorStorage storage) {
    super(storage);
  }

  @Override
  public InputFile inputFile() {
    return inputFile;
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  public TextPointer location() {
    return location;
  }

  @Override
  public NewAnalysisError onFile(InputFile inputFile) {
    checkArgument(inputFile != null, "Cannot use a inputFile that is null");
    checkState(this.inputFile == null, "onFile() already called");
    this.inputFile = inputFile;
    return this;
  }

  @Override
  public NewAnalysisError message(String message) {
    this.message = message;
    return this;
  }

  @Override
  public NewAnalysisError at(TextPointer location) {
    checkState(this.location == null, "at() already called");
    this.location = location;
    return this;
  }

  @Override
  protected void doSave() {
    requireNonNull(this.inputFile, "inputFile is mandatory on AnalysisError");
    storage.store(this);
  }

}
