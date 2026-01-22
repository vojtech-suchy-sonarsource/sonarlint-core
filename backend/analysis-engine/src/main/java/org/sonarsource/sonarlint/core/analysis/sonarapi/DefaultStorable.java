/*
ACR-0699d0d858a641c7ac7f3e6655a1a3c8
ACR-d3d257729309430cad8b74980539f1a9
ACR-d1722fceab6e4fb1b0eb1984ae2d754a
ACR-27e1dd865a734543825172e60bf034e0
ACR-0bf5ea05dcda4e8ebdbf4bf0c749dedb
ACR-630bfc06ecbc48eb85e9d1f0b0de269c
ACR-fd0973e099424495a65c600fc23ea883
ACR-480080cb37054c4db4f920380ea211ed
ACR-4aba6dacb81f40338f009647b0415a46
ACR-7062826ad92a403d9791a3cbff1cd2a2
ACR-dbd144ae43654357ad50a15144dd7e13
ACR-b3394705d4014ce88835a13a778cfa51
ACR-28cdd04bf89944878742a78a3c8e56d7
ACR-916f958953574a33a3a9d9c64ef08827
ACR-a00798d249be4f48980236fea4705fa7
ACR-1131f71c5ded4a2388d64d1939f3ffb4
ACR-32f306870ee446edb8e3df149ff069a9
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
