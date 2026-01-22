/*
ACR-3a181e6a69054e6ab032644f6c6cfbea
ACR-311214b702454c3490812177bb86340b
ACR-986f3001dba5418a8a5b84c0f337fc24
ACR-d6f31c46be3940fdbb1d07a8b05aa007
ACR-b0b5293b4ae14eabb2ac98c7a6ec2416
ACR-e00e43bb76cb4be0b1a00542aa7d91c0
ACR-e0ebc23031e64266b782ae02b74ceec7
ACR-eafce15021a84270b09adbceaf1abb6a
ACR-1f0f5e4ece1947c4ba168f3d18491846
ACR-c972a24cdea54ac2a65289b65ef92429
ACR-14df89bade8c4d9d8dd6f684e1eae9b2
ACR-7caf72b09d7c49e1b48c34feb35547af
ACR-ddfe7aa0abfa415b8cb77b69250ff4fe
ACR-e0ee542aaa6e4b1b8b2028510956d520
ACR-478a2c01174349d8beb6407d86f497da
ACR-0d0bd043a735486588110373c8933a03
ACR-570e8f487e004872ad5afdf52a94d303
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;

public class DefaultTextRange implements TextRange {

  private final TextPointer start;
  private final TextPointer end;

  public DefaultTextRange(TextPointer start, TextPointer end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public TextPointer start() {
    return start;
  }

  @Override
  public TextPointer end() {
    return end;
  }

  @Override
  public boolean overlap(TextRange another) {
    //ACR-974ccf700375431abc8824d7d8b4f1fb
    //ACR-bac8c8d3c17e4dd4bf60224e5e3bebc0
    return this.end.compareTo(another.start()) > 0 && another.end().compareTo(this.start) > 0;
  }

  @Override
  public String toString() {
    return "Range[from " + start + " to " + end + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var other = (DefaultTextRange) obj;
    return start.equals(other.start) && end.equals(other.end);
  }

  @Override
  public int hashCode() {
    return start.hashCode() * 17 + end.hashCode();
  }

}
