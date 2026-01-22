/*
ACR-b483e18a9f334e1c95a35557e45e5f7f
ACR-e067f3b492a640649bff70ffa5f7a37d
ACR-4fac79cf4b39418db41d1368bb189450
ACR-e56a14a807a54f5eb1d3817d63a2abc0
ACR-34404915495145cf94f2a64b4568f0b8
ACR-918f33096bd0448aa297549280afbaae
ACR-aa0123be11a1426d987bffa99efe8604
ACR-65a7d4082abc4458ab21bc10d7f627e1
ACR-216e4a09b3204190909bd51c68ad36f7
ACR-f6e66cda434243d58bcafc92c81caa2f
ACR-fd6823170ec74d27b22030b66b6c7ffc
ACR-ece5e74d1b404164ba8bb607afddfac0
ACR-ef4c2790c5ed4c23a27dcc94a0269284
ACR-77ee4cb66cd1467eb8970c0a26c27015
ACR-36688aaadeb14a59bf88806b2462a53b
ACR-8994cecfd54b4482b9a374114cb44bdf
ACR-c29cfe34be7b409fb94229d87b83969d
 */
package org.sonarsource.sonarlint.core.commons;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface NewCodeDefinition {

  String DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
  DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

  NewCodeMode getMode();

  boolean isOnNewCode(long creationDate);

  default boolean isOnNewCode(Instant introductionDate) {
    return isOnNewCode(introductionDate.toEpochMilli());
  }

  boolean isSupported();

  static String formatEpochToDate(long epoch) {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()).format(DATETIME_FORMATTER);
  }

  static NewCodeDefinition withAlwaysNew() {
    return new NewCodeAlwaysNew();
  }

  static NewCodeDefinition withExactNumberOfDays(int days) {
    return new NewCodeExactNumberOfDays(days);
  }

  /*ACR-2bea37f19390433f984af44b7583b03a
ACR-e5be59ea2d2b49fdb3d5760c3aae632f
ACR-509874d064e1438d87077fcba937d74d
   */
  static NewCodeDefinition withNumberOfDaysWithDate(int days, long thresholdDate) {
    return new NewCodeNumberOfDaysWithDate(days, thresholdDate);
  }

  static NewCodeDefinition withPreviousVersion(long thresholdDate, @Nullable String version) {
    return new NewCodePreviousVersion(thresholdDate, version);
  }

  static NewCodeDefinition withReferenceBranch(String referenceBranch) {
    return new NewCodeReferenceBranch(referenceBranch);
  }

  static NewCodeDefinition withSpecificAnalysis(long thresholdDate) {
    return new NewCodeSpecificAnalysis(thresholdDate);
  }

  Instant getThresholdDate();

  abstract class NewCodeDefinitionWithDate implements NewCodeDefinition {
    protected final long thresholdDate;

    protected NewCodeDefinitionWithDate(long thresholdDate) {
      this.thresholdDate = thresholdDate;
    }

    public boolean isOnNewCode(long creationDate) {
      return creationDate > thresholdDate;
    }

    public boolean isSupported() {
      return true;
    }

    public Instant getThresholdDate() {
      return Instant.ofEpochMilli(thresholdDate);
    }
  }

  class NewCodeExactNumberOfDays implements NewCodeDefinition {
    private final int days;

    public NewCodeExactNumberOfDays(int days) {
      this.days = days;
    }

    @Override
    public NewCodeMode getMode() {
      return NewCodeMode.NUMBER_OF_DAYS;
    }

    @Override
    public boolean isOnNewCode(long creationDate) {
      return creationDate > Instant.now().minus(days, ChronoUnit.DAYS).toEpochMilli();
    }

    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public Instant getThresholdDate() {
      return Instant.now().minus(days, ChronoUnit.DAYS);
    }

    //ACR-a0e9735fa4fc44b981a714e7222130b8
    @Override
    public String toString() {
      return String.format("From last %s days", days);
    }
  }

  class NewCodeNumberOfDaysWithDate extends NewCodeDefinitionWithDate {
    Integer days;

    private NewCodeNumberOfDaysWithDate(Integer days, long thresholdDate) {
      super(thresholdDate);
      this.days = days;
    }

    //ACR-85d7123d82dd4abeab3bb09677788b5e
    @Override
    public String toString() {
      return String.format("From last %s days", days);
    }

    @Override
    public NewCodeMode getMode() {
      return NewCodeMode.NUMBER_OF_DAYS;
    }

    public Integer getDays() {
      return days;
    }
  }

  class NewCodePreviousVersion extends NewCodeDefinitionWithDate {
    private final String version;

    private NewCodePreviousVersion(long thresholdDate, @Nullable String version) {
      super(thresholdDate);
      this.version = version;
    }

    //ACR-03c8a19806c64207b8200e77f21e2b90
    @Override
    public String toString() {
      var versionQualifier = (version == null) ? formatEpochToDate(this.thresholdDate) : ("version " + version);
      return String.format("Since %s", versionQualifier);
    }

    @Override
    public NewCodeMode getMode() {
      return NewCodeMode.PREVIOUS_VERSION;
    }

    @CheckForNull
    public String getVersion() {
      return version;
    }
  }

  class NewCodeSpecificAnalysis extends NewCodeDefinitionWithDate {
    private NewCodeSpecificAnalysis(long thresholdDate) {
      super(thresholdDate);
    }

    //ACR-c46adb2715b64499b662df753999aa2b
    @Override
    public String toString() {
      return String.format("Since analysis from %s", formatEpochToDate(this.thresholdDate));
    }

    @Override
    public NewCodeMode getMode() {
      return NewCodeMode.SPECIFIC_ANALYSIS;
    }
  }

  class NewCodeReferenceBranch implements NewCodeDefinition {
    private final String branchName;

    private NewCodeReferenceBranch(String branchName) {
      this.branchName = branchName;
    }

    @Override
    public NewCodeMode getMode() {
      return NewCodeMode.REFERENCE_BRANCH;
    }

    @Override
    public boolean isOnNewCode(long creationDate) {
      return true;
    }

    @Override
    public boolean isSupported() {
      return false;
    }

    public String getBranchName() {
      return branchName;
    }

    @Override
    public Instant getThresholdDate() {
      //ACR-8639d9486563492ab78dd1d3d36fd106
      return Instant.now();
    }

    //ACR-eb87053f4668449090660c3cd0b43b9a
    @Override
    public String toString() {
      return "Current new code definition (reference branch) is not supported";
    }
  }

  class NewCodeAlwaysNew implements NewCodeDefinition {

    private NewCodeAlwaysNew() {
      //ACR-fdf0e01a36044486a21bd62443996584
    }

    @Override
    public NewCodeMode getMode() {
      throw new UnsupportedOperationException("Mode shouldn't be called for this new code definition");
    }

    @Override
    public boolean isOnNewCode(long creationDate) {
      return true;
    }

    @Override
    public Instant getThresholdDate() {
      //ACR-f030e1254d2c47abbcf1c28e716f4c94
      return Instant.now();
    }

    @Override
    public boolean isSupported() {
      return true;
    }
  }
}
