/*
ACR-e25da19614a74dd780e6cbc01481b70e
ACR-a1306f9c15d04030ab37f2de1d1a1c37
ACR-0f5b247515d74d3db73757866e9ea252
ACR-1053d5b80cbc415595d3bd1d6dddee74
ACR-926592001f3b404ba330b21f0b043f94
ACR-5c0aa32295db4892a55c92333fcedafd
ACR-4ce579e926064a58ad6be842fae876c6
ACR-e1a49851bb6b4af6ad162659e0f2db74
ACR-2d898180c1ee46ba850a474b69deef38
ACR-52069a975e294e2192d5084daa2e1179
ACR-6adfad5ce2cc4f55be7054d81506f042
ACR-d5600b554e8145f9982ef639729b8fac
ACR-29adc30199084dfcab6c81b40c5551dc
ACR-b3a8a75ffcfa421fb0dfe33c932ec439
ACR-0c68700ead2140298ba34261f7da70d5
ACR-16933bb210e547b3beba7d5700cf0efc
ACR-d8a62c6db0914b95bad535e6ea406dff
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
