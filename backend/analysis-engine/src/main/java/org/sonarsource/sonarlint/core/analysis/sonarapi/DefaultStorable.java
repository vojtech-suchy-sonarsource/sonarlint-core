/*
ACR-a661d29045c84fa29722520ebab54066
ACR-1bd74c337b35453daba792d19ebe8ab7
ACR-d0d41277d48d483c97841f152afff110
ACR-2657af0f733d456dbb09de405bbac44b
ACR-230a9601fdb3422a9737b4ba73d009b1
ACR-2bd61b5712f44d518f213c24f804e70f
ACR-518b7d95afa644eeae8ebab5eb61ebe1
ACR-d1e1accdccbc4093b70b2b7becc3ff34
ACR-c304a8dc9bf9451da2205427e2ff8eba
ACR-8c60e26e7b824c1ba9442e3be1c48fed
ACR-16333b98fc674578851a6f84ba2821f8
ACR-ebcf791d701c4692b5c7575f99e18fcb
ACR-b4ad302622e6409fbe14d196451c374e
ACR-282c7cb8a8424795ba9c0789cdfa0390
ACR-997525deee2544b29bf04cc4091beaaf
ACR-9ee979c03be749609fd04cd795da40ef
ACR-c23fbabf488640caa4ced614a6012138
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sonar.api.batch.sensor.internal.SensorStorage;

import static java.util.Objects.requireNonNull;
import static org.sonar.api.utils.Preconditions.checkState;

abstract class DefaultStorable {

  protected final SensorStorage storage;
  private boolean saved = false;

  protected DefaultStorable(@Nullable SensorStorage storage) {
    this.storage = storage;
  }

  public final void save() {
    requireNonNull(this.storage, "No persister on this object");
    checkState(!saved, "This object was already saved");
    doSave();
    this.saved = true;
  }

  protected abstract void doSave();

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
