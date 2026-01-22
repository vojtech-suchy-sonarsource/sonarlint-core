/*
ACR-80c74a73bd8a4b7eac23d902db6b9032
ACR-0129c895040340b2823c2453daf0a159
ACR-e14e778f16e543dd8593d44cd738c179
ACR-0649a48930264738ab9f74ed74c4abf5
ACR-eb3999da659e4ecea838cb609a585eb8
ACR-6e896de1df554b5aa720cb8ca1e01167
ACR-55790de8b2534bbba87f275515d559bf
ACR-b6ea563e7a81419e88bf401035d976c9
ACR-05cb16bec7344e4a9164e3a1ccf98c99
ACR-4064b50826414f5382a2d4dee68d800d
ACR-379dc8002b7d4a989c52b557ed64c3ed
ACR-9f3b777f31904a0fa347a6414916b51b
ACR-75f87907c96e4309bfc4c33a922c5e28
ACR-6b1446ef60854caea5b394bd731f163e
ACR-38398cfeecd746c3ba3361d6e59a69b4
ACR-bd739e7899c2427fb93c657b53de7c74
ACR-60aa9dd61ea847ce944839c77d9af490
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class LineRange {
  private final int from;
  private final int to;

  public LineRange(int line) {
    this(line, line);
  }

  public LineRange(int from, int to) {
    if (from > to) {
      throw new IllegalArgumentException(String.format("Line range is not valid: %s must be greater or equal than %s", to, from));
    }

    this.from = from;
    this.to = to;
  }

  public boolean in(int lineId) {
    return from <= lineId && lineId <= to;
  }

  public Set<Integer> toLines() {
    Set<Integer> lines = new LinkedHashSet<>(to - from + 1);
    for (var index = from; index <= to; index++) {
      lines.add(index);
    }
    return lines;
  }

  public int from() {
    return from;
  }

  public int to() {
    return to;
  }

  @Override
  public String toString() {
    return "[" + from + "-" + to + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    return !fieldsDiffer((LineRange) obj);
  }

  private boolean fieldsDiffer(LineRange other) {
    return from != other.from || to != other.to;
  }
}
