/*
ACR-7c8f59a1822d4c2b8eeb8abad37e1486
ACR-883bad65929e4260893532a71d8655f0
ACR-fa7e0eed990c44698edcc11e395277f0
ACR-8da2c672f5474abf8717adb431c947a6
ACR-d8827ee2b04b4c139a1968ddf057409d
ACR-3879fccb78bd49b2a7dffdac2509bd46
ACR-b49f1e188c014cd5b3e91e11f66dff05
ACR-b05a2f6b29754b588003df3326673311
ACR-77acab83a4734d81b41b390cea95be70
ACR-fc00e09864d34d52b0db727192ffce97
ACR-1542be89ab15440b89b0788c914f2819
ACR-a6d0a763d8fa40ddb93c42dfe2594959
ACR-b73ac3ad3e764e57a86aa11f1f3898a4
ACR-3f81c33aab0f496184f118cba685ab8a
ACR-0ea1f42fe29a4bbcbd4bd54201e24090
ACR-60c96c2e01f34df8bd8456e4864e4b60
ACR-955c8a4f5f744cfc87a6669d1205170b
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
    //ACR-eee73c0ebdd54f95b139537ab0b444fc
    //ACR-7ed68f2595324801a50f449b8c02b1fb
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
