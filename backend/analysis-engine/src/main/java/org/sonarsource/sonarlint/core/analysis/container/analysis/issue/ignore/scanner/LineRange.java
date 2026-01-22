/*
ACR-e24786e9458f43b4bd1e4d8dc4906366
ACR-d2f82df1e01d45b8b04d12ebaf087641
ACR-292bce6153214e509e844df1ecc00041
ACR-631b562b662a4bd897bc5e6f48422748
ACR-7cd1f6e987994285908ce2724c9a4155
ACR-bd1e71ab5fae490ab3b0e42eb66dc78c
ACR-5cd8359dd55f41758effb9c6e187fc9b
ACR-bcfbd4efc27e45378a57f30270a7e7b1
ACR-3c1e7f1128c346068f341eeafb5cbe74
ACR-c3b7ec68840a48db8d79b77e58959ced
ACR-b712fda377a44952ab9f65815f4dd74e
ACR-d2ea9183486540b68bba56132f78fe23
ACR-4d304a6034cd4603bc22e5e88bbe87bd
ACR-80e1fa702fc34958bc75eaec40e7278c
ACR-a687fe29490b4d2cacca9a7f8735cd9e
ACR-f269823c549f4a5586ad814a25f81775
ACR-98bfc268e3f34d708316629932946d32
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
