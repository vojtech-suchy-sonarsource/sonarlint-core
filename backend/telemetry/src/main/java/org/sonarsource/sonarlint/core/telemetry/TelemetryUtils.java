/*
ACR-db60525484654fb9a7426b37d633660e
ACR-3b5de08bde97491bac41818cafc2c03c
ACR-1f598fbb978145bb97f2a2a1ea9be623
ACR-faf21a2576654e6fafbcd3836c4e2aff
ACR-27cae8b4bce843c6bc05fdf9cf70706a
ACR-16025958ef4a459090f8fe92c929b7d2
ACR-1b9d62d481784bfb86441b592ce6fca3
ACR-2fbfc5a2478840a281a9bde65a5722c8
ACR-53b55c4f334b4a268d574bad1770ad66
ACR-ae286ad1888c4c8b8e14529c88a968a2
ACR-e2f9340f3e034a08878a6bf21079fa4e
ACR-6cd5f9ac39604b2fb0fdfc450dd0504d
ACR-76dddf3b3bdc41ae963a7915b761609d
ACR-7fc8ae17bf5e415da8622c096dc299e9
ACR-9a4995b2407a42f7a1f93f2abd990d50
ACR-864a713597ea4dfea62392e0e2bef8ec
ACR-e858d15f19454c49972e05f8f2c2d32a
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryAnalyzerPerformancePayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryFixSuggestionPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryFixSuggestionResolvedPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryNotificationsCounterPayload;
import org.sonarsource.sonarlint.core.telemetry.payload.TelemetryNotificationsPayload;

class TelemetryUtils {

  private TelemetryUtils() {
    //ACR-2456bd1e491e4f3f94accac8bcd42a41
  }

  /*ACR-66b782dda86740c7b411dca7f342aff6
ACR-7bc8b0b934b54d02acbaa63142efd233
ACR-835fdd0ff4fd4907a931d771df8e48fd
ACR-b32497fc2fe8422d8533ec1ae7445316
ACR-645d1cb4e73541c88245f41d7569e9f7
   */
  static boolean isGracePeriodElapsedAndDayChanged(@Nullable LocalDate date) {
    return date == null || !date.equals(LocalDate.now());
  }

  /*ACR-0811c72483de4b768ef9dcd190cabbd5
ACR-67fcf8e0698346ad96798c78e3333556
   */
  static TelemetryAnalyzerPerformancePayload[] toPayload(Map<String, TelemetryAnalyzerPerformance> analyzers) {
    return analyzers.entrySet().stream()
      .map(TelemetryUtils::toPayload)
      .toArray(TelemetryAnalyzerPerformancePayload[]::new);
  }

  private static TelemetryAnalyzerPerformancePayload toPayload(Map.Entry<String, TelemetryAnalyzerPerformance> entry) {
    var analyzerPerformance = entry.getValue();
    var language = entry.getKey();
    var analysisCount = analyzerPerformance.analysisCount();
    Map<String, BigDecimal> distribution = analyzerPerformance
      .frequencies().entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> {
        if (analysisCount == 0) {
          return BigDecimal.ZERO.setScale(2);
        }
        return BigDecimal.valueOf(100)
          .multiply(BigDecimal.valueOf(e.getValue()))
          .divide(BigDecimal.valueOf(analysisCount), 2, RoundingMode.HALF_EVEN);
      }, throwingMerger(), LinkedHashMap::new));

    return new TelemetryAnalyzerPerformancePayload(language, distribution);
  }

  static TelemetryNotificationsPayload toPayload(boolean devNotificationsDisabled, Map<String, TelemetryNotificationsCounter> notifications) {
    return new TelemetryNotificationsPayload(devNotificationsDisabled, toNotifPayload(notifications));
  }

  private static Map<String, TelemetryNotificationsCounterPayload> toNotifPayload(Map<String, TelemetryNotificationsCounter> notifications) {
    return notifications.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
      e -> new TelemetryNotificationsCounterPayload(e.getValue().getDevNotificationsCount(), e.getValue().getDevNotificationsClicked())));
  }

  static TelemetryFixSuggestionPayload[] toFixSuggestionResolvedPayload(
    Map<String, TelemetryFixSuggestionReceivedCounter> fixSuggestionReceivedCounter,
    Map<String, List<TelemetryFixSuggestionResolvedStatus>> fixSuggestionResolved
  ) {
    return fixSuggestionReceivedCounter.entrySet().stream().map(e -> {
      var suggestionId = e.getKey();
      var snippetsCount = e.getValue().snippetsCount();
      var source = e.getValue().aiSuggestionsSource();
      var resolvedSnippetStatus = fixSuggestionResolved.getOrDefault(suggestionId, List.of(new TelemetryFixSuggestionResolvedStatus(null, null)));
      var resolvedSnippetPayload = resolvedSnippetStatus.stream()
        .map(s -> new TelemetryFixSuggestionResolvedPayload(s.getFixSuggestionResolvedStatus(),
        s.getFixSuggestionResolvedSnippetIndex())).toList();
      var wasGeneratedFromIde = e.getValue().wasGeneratedFromIde();

      return new TelemetryFixSuggestionPayload(suggestionId, snippetsCount, source, resolvedSnippetPayload, wasGeneratedFromIde);
    }).toArray(TelemetryFixSuggestionPayload[]::new);
  }

  /*ACR-960397ecd9e047719b8fcb42c24cfdf2
ACR-8ff9e9490a984169b24da81dd836b0c1
ACR-2c272597c36e4d4ca1949182ba2ed329
ACR-fc75a22b408140a3a28eccc6726953d8
ACR-7014235cca7d4608a1a6e2b3d6b34797
ACR-0f5fc22cef8743939668973ab902433a
   */
  static boolean isGracePeriodElapsedAndDayChanged(@Nullable LocalDateTime dateTime, long hours) {
    return dateTime == null ||
      (!LocalDate.now().equals(dateTime.toLocalDate())
        && (dateTime.until(LocalDateTime.now(), ChronoUnit.HOURS) >= hours));
  }

  private static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }

}
