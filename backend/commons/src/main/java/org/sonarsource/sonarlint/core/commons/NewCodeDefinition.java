/*
ACR-622b3cc2c9344cf19ca2a36ba816ad82
ACR-8c8091894d9c4807a0ea5b453205c850
ACR-3a9f5de261a446268aee5d311e1be72b
ACR-951b9f1174564fd99c9beba935d5afd9
ACR-800a63600c5744928fe5594b71c0e245
ACR-ba68a5d319654ee8971d7adf740f700c
ACR-0ce4b3e880e44596a9b5abdfe542d395
ACR-a494339304f946f1b512f988191e3b3f
ACR-23d242fb59754c70869dc0ceffa0fa9b
ACR-9b2eb1bcd0df4c26b135fb9e6cb1cdcb
ACR-865ceb3d00e0418584681a566ab1ec41
ACR-7066126998b54ca1a6630b06b51ee95f
ACR-669378739bc144efb3c7bd66a72cdc86
ACR-6fa05d54d0ce4f0ab4550831641419f8
ACR-0380ef14c5dd438e990bb661c83ebfe6
ACR-a21d28345e5c45f8a1d1f4c32d4e5620
ACR-1ba7982f25714f38a4348084ac673de9
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

  /*ACR-21994e9c526f4e33b0e904f64f607468
ACR-c2208e0106db4bc785cbaf2fc1ee3ea8
ACR-c3b26fdd734f4005bbac179436ed6762
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

    //ACR-d44856551d724de7a150cc07ef2ffd24
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

    //ACR-f0b65201a2f142108b020bd8b8964e71
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

    //ACR-6c922ef0c6c74e3998b6e42827759349
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

    //ACR-b027eb59a10d443ca5b54e62002ae2db
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
      //ACR-5e92bb8b3f1a44fbb6717f7e71cc2535
      return Instant.now();
    }

    //ACR-27fdd64e5e994f73b46e4590bcde0568
    @Override
    public String toString() {
      return "Current new code definition (reference branch) is not supported";
    }
  }

  class NewCodeAlwaysNew implements NewCodeDefinition {

    private NewCodeAlwaysNew() {
      //ACR-12a4905e76084db8a4ea06af709f89eb
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
      //ACR-cd5676754e7b48edbc2716587e662eaa
      return Instant.now();
    }

    @Override
    public boolean isSupported() {
      return true;
    }
  }
}
