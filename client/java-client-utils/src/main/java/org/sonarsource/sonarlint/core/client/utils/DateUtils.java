/*
ACR-a06b77ddeb094330aebd6c4044dbbf95
ACR-9bc31f2e47444d4e8ae8832d356d3035
ACR-59aff5d157a44191940582df250ed339
ACR-4440e014187b41e0aea546f62632bac7
ACR-9351bb3bd5514a20820777d830f68533
ACR-e962e452f4954dc48deb57f517f1e75e
ACR-1d387ae8de3d47e4b25661ea2e2903d6
ACR-3bb7fb5011a3439dae7850741785f384
ACR-75999b4e1e2b4a14a66d53a89f8236b8
ACR-ae763c062210482b9bc3e4602d23c444
ACR-130b8072ea5d4e3db74dcc1b0d75ca8c
ACR-58fb658b7e6546c59988ec84c7ccd3f6
ACR-2ec5f7e613fc4470bd4542945df11685
ACR-75a66fbeb89346bea65d4d21fdbca070
ACR-62712e1fe3d14bfdbf2a543a90d3ff48
ACR-5bcead37e100426c91b8bf001e0599dd
ACR-bf441ff190534ac280df01f9a3731018
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateUtils {

  private DateUtils() {
    //ACR-197bc30e73db4c4381d8ce3eaac7a8fe
  }

  public static String toAge(long time) {
    var creation = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    var now = LocalDateTime.now();

    var years = ChronoUnit.YEARS.between(creation, now);
    if (years > 0) {
      return pluralize(years, "year");
    }
    var months = ChronoUnit.MONTHS.between(creation, now);
    if (months > 0) {
      return pluralize(months, "month");
    }
    var days = ChronoUnit.DAYS.between(creation, now);
    if (days > 0) {
      return pluralize(days, "day");
    }
    var hours = ChronoUnit.HOURS.between(creation, now);
    if (hours > 0) {
      return pluralize(hours, "hour");
    }
    var minutes = ChronoUnit.MINUTES.between(creation, now);
    if (minutes > 0) {
      return pluralize(minutes, "minute");
    }

    return "few seconds ago";
  }

  private static String pluralize(long strictlyPositiveCount, String singular) {
    return pluralize(strictlyPositiveCount, singular, singular + "s");
  }

  private static String pluralize(long strictlyPositiveCount, String singular, String plural) {
    if (strictlyPositiveCount == 1) {
      return "1 " + singular + " ago";
    }
    return strictlyPositiveCount + " " + plural + " ago";
  }
}
