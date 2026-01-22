/*
ACR-2023cdbb5c65467694841b3126a510cb
ACR-e5f41c88054b470bb1ccbc7c3200d83b
ACR-a604545761944732a5820e98de828c3e
ACR-76ee886dbf5748ce92513b82c1656b2c
ACR-ea25aa8a2ef64bc1882df71b34c7e699
ACR-4fd5f28e6e4a464ea44f48fa78e22423
ACR-52fffb0f5fe54f669a907034b22c0d8a
ACR-cce09489796d411eafb37cd4daa012d3
ACR-c1c74696bf7645b5857e3ffaacbe0b7f
ACR-2fd15bcf1841406f91caef1e970c7eca
ACR-346c1d0122394008a484833781da110f
ACR-f09a7180e58343fcafb3e7e587b4e77e
ACR-30203c086ba54524ba8b13a6773e4355
ACR-63d081c39be44af09571fd4e953feb0e
ACR-5b108d43e6f64162ae3d4ba5ac1d1ea5
ACR-a5c8ca3515894228bf312c918ecc49b9
ACR-6f081a324b3c44f8b699e5138caeaec7
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
    //ACR-5757287833194e3b8539cf8b478822d8
  }

  /*ACR-7e5e0e9cb90b484599c60e712f023379
ACR-58987a7c051a4e559fdc3cbde5333861
ACR-83f12e3cc0e4429da512009307e9a465
ACR-fbb47c67b2f44a05a243da43926a7c29
ACR-fe9e4eab753b4060b17d526fa02b8d9d
   */
  static boolean isGracePeriodElapsedAndDayChanged(@Nullable LocalDate date) {
    return date == null || !date.equals(LocalDate.now());
  }

  /*ACR-1859162cde2648a1a81328dc03036b53
ACR-55a079f215b547ceb0aee8b42a98f19c
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

  /*ACR-17f9b409ea6d49968cb9e292108bf0ed
ACR-ef21e3ae39b74f229827a5c7c0399edf
ACR-dbe74e81d4814253b94eb0a546d4fb4e
ACR-4f130ba0a4874a84acea1b7c55f6812e
ACR-4378cfd568cb4bcb81222e03f95fc56f
ACR-18e1193dabbd4b38817943b71338f3c6
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
