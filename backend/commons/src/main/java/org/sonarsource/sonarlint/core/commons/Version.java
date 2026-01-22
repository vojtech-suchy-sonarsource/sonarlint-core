/*
ACR-45a16127485b46f0add00c6270898a66
ACR-3622bf400f294fd6b452199920e6da05
ACR-c0706fed739f41a6887e61cb5f23d170
ACR-7aca129550514307ada38e15faa1a94b
ACR-bb4be25dbd6243c797aeec0deb85131d
ACR-cc1b3be13f704bfe81522c666487d6a3
ACR-249bef35e8b04e87957b839139c7e95d
ACR-236ac447a8fd4becabc4ee11da9d37af
ACR-c1985f0d9965405292cb7033c8ff7b99
ACR-3d05c1ff3ff14978bd2a38eefa554f19
ACR-ac2a95bde85c4188840bef7410cd5db2
ACR-a0588096fd194edf97d0d3a695a5d54b
ACR-9cafaeb437af4eefba7bb37a1083dbdf
ACR-7185d60fb9d04d0e912e95b73354c661
ACR-653b80b4c6c3408dbb50fdd09f649e54
ACR-dc6bd9ab31514b3380fcc475e7dcb338
ACR-c7ab15e7c9254baca9faf9cae35d4b98
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.Arrays;

public class Version implements Comparable<Version> {

  private final String name;
  private final String nameWithoutQualifier;
  private final int[] numbers;
  private final String qualifier;

  private Version(String version) {
    this.name = version.trim();
    var qualifierPosition = name.indexOf("-");
    if (qualifierPosition != -1) {
      this.qualifier = name.substring(qualifierPosition + 1);
      this.nameWithoutQualifier = name.substring(0, qualifierPosition);
    } else {
      this.qualifier = "";
      this.nameWithoutQualifier = this.name;
    }
    final var split = this.nameWithoutQualifier.split("\\.");
    numbers = new int[split.length];
    for (var i = 0; i < split.length; i++) {
      numbers[i] = Integer.parseInt(split[i]);
    }
  }

  private Version(String name, String nameWithoutQualifier, int[] numbers, String qualifier) {
    this.name = name;
    this.nameWithoutQualifier = nameWithoutQualifier;
    this.numbers = Arrays.copyOf(numbers, numbers.length);
    this.qualifier = qualifier;
  }

  public int getMajor() {
    return numbers.length > 0 ? numbers[0] : 0;
  }

  public int getMinor() {
    return numbers.length > 1 ? numbers[1] : 0;
  }

  public int getPatch() {
    return numbers.length > 2 ? numbers[2] : 0;
  }

  public int getBuild() {
    return numbers.length > 3 ? numbers[3] : 0;
  }

  public String getName() {
    return name;
  }

  public String getQualifier() {
    return qualifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Version other)) {
      return false;
    }
    return getMajor() == other.getMajor()
      && getMinor() == other.getMinor()
      && getPatch() == other.getPatch()
      && getBuild() == other.getBuild()
      && qualifier.equals(other.qualifier);
  }

  @Override
  public int hashCode() {
    var result = Integer.hashCode(getMajor());
    result = 31 * result + Integer.hashCode(getMinor());
    result = 31 * result + Integer.hashCode(getPatch());
    result = 31 * result + Integer.hashCode(getBuild());
    result = 31 * result + qualifier.hashCode();
    return result;
  }

  @Override
  public int compareTo(Version other) {
    var c = compareToIgnoreQualifier(other);
    if (c == 0) {
      if ("".equals(qualifier)) {
        c = "".equals(other.qualifier) ? 0 : 1;
      } else if ("".equals(other.qualifier)) {
        c = -1;
      } else {
        c = qualifier.compareTo(other.qualifier);
      }
    }
    return c;
  }

  public int compareToIgnoreQualifier(Version other) {
    var maxNumbers = Math.max(numbers.length, other.numbers.length);
    var myNumbers = Arrays.copyOf(numbers, maxNumbers);
    var otherNumbers = Arrays.copyOf(other.numbers, maxNumbers);
    for (var i = 0; i < maxNumbers; i++) {
      var compare = Integer.compare(myNumbers[i], otherNumbers[i]);
      if (compare != 0) {
        return compare;
      }
    }
    return 0;
  }

  @Override
  public String toString() {
    return name;
  }

  public static Version create(String version) {
    return new Version(version);
  }

  public Version removeQualifier() {
    return new Version(nameWithoutQualifier, nameWithoutQualifier, numbers, "");
  }

  public boolean satisfiesMinRequirement(Version minRequirement) {
    return this.compareToIgnoreQualifier(minRequirement) >= 0;
  }
}
