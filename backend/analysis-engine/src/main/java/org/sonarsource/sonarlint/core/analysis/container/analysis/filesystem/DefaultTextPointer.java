/*
ACR-7d2417989faa439590bdb8d4ac1b5850
ACR-feadcad5bf944809a3428b6afc724d9b
ACR-17cda1c6b5df4d2b92b45b74b9e2d66c
ACR-8af17f153e8b41bcb634572488b90273
ACR-52ab73006a2042138448a7273ff48110
ACR-174f2fbdd6ac4172bfc132a8478ee55c
ACR-afd25551b275474e9f4c6fe4b9f1e022
ACR-6bef21e4b14949889e7d17f29692dd76
ACR-12290b2f81d94d86b479dee3c62a9cb2
ACR-e90a3575b7014b2c80e2451637cdbf51
ACR-edffeb74c3ce40de92494b7b11bfc53d
ACR-f3af8a70a1a24ffb85e28d9257cc667b
ACR-57483facb4ee4a10b9a6787c9854bf72
ACR-3a8c2960ebaa47a1a3dff43e69fb5357
ACR-3aa43d0077e14b8cb9a5a8e7238bd522
ACR-f26c756c3f5646a1abcb0fa1969a2912
ACR-85d4a97940104165b27073c14d623fc7
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.TextPointer;

public class DefaultTextPointer implements TextPointer {

  private final int line;
  private final int lineOffset;

  public DefaultTextPointer(int line, int lineOffset) {
    this.line = line;
    this.lineOffset = lineOffset;
  }

  @Override
  public int line() {
    return line;
  }

  @Override
  public int lineOffset() {
    return lineOffset;
  }

  @Override
  public String toString() {
    return "[line=" + line + ", lineOffset=" + lineOffset + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var other = (DefaultTextPointer) obj;
    return other.line == this.line && other.lineOffset == this.lineOffset;
  }

  @Override
  public int hashCode() {
    return 37 * this.line + lineOffset;
  }

  @Override
  public int compareTo(TextPointer o) {
    if (this.line == o.line()) {
      return Integer.compare(this.lineOffset, o.lineOffset());
    }
    return Integer.compare(this.line, o.line());
  }

}
