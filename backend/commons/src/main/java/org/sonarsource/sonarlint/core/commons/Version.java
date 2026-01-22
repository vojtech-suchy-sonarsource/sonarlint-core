/*
ACR-df085441e76a42e8b8497ce4c9628d39
ACR-8509754bd2744afcb66ac7641a8b465b
ACR-8140f901cb7740a5a0d9de2fca51d8ed
ACR-d9b1f892743e4eb5aa4e298a1806021b
ACR-8a4bb9ccfe124d959be5f48d898a1b24
ACR-b4c2d04b572a4c3db497d27b5a2932c6
ACR-56de904ee7374e3797d5e2e0f3dc57e0
ACR-954d97f193cc4fc084076b970f1938aa
ACR-bd40a8d80f4d4fef995923cbeec7778c
ACR-fb503dbacb3547f5815b867cfce8313a
ACR-eaadf38dda8a45cabc1f70291146f776
ACR-0fa2db80b2ec4160ac09d7bd8666e14f
ACR-461d1470d98a4752b4d07b427a49b8a4
ACR-d166271002014d4595d5df97f00545b1
ACR-fac4e4d1f54c43ffa58681d66e6d3e71
ACR-7725e8d628154767b6e65b22341b25ca
ACR-b279bb529fce4c55b07e4fc3019854cb
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
