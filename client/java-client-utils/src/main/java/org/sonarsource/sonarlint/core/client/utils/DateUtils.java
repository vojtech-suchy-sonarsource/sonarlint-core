/*
ACR-e4fab8ef71ab4d3d99dcb664f3c19ab1
ACR-80c831c578e14c95a7a0dcc661ee5232
ACR-bfcfdae193dd4858aa2155e37f09c234
ACR-4d949ea2e0484f6593779ab948ce5492
ACR-08132bd53e654c55b43dfbc082af90ca
ACR-3a01793e104b4a3096a6aabd66aca8ce
ACR-c13ca9426343470494fbf57537ca2da2
ACR-5053ef38748a41bfb51664546998b311
ACR-acc30041857140fe8cf677a1b4ec6380
ACR-28c672791244474ead0585ab3b130e66
ACR-c574364e196a4a3597610fcf74c19fb9
ACR-69b42edb7b0f48b7844b40083b6a6203
ACR-cf0a638c88784a8fa50b2c03cdc87de6
ACR-adae760879ed40318d88ee50657ffa93
ACR-32fe216d45c84f42ac1dd1a35104dbcf
ACR-70d464e18eb2415bbb8b6e22b8ad94ce
ACR-ce3a0b20216d4f558687bbe7a259af69
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateUtils {

  private DateUtils() {
    //ACR-8724f4d174394956aba7dee0645ad21c
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
