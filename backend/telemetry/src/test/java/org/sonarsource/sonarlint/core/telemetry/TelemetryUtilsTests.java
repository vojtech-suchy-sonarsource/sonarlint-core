/*
ACR-cd4e7c5b85ef420ca41ceb766e2e8e8c
ACR-764fad46c63d459fbc903e79c54a1095
ACR-52305fca836947a7af334ebaefd4b4cf
ACR-93ba5a3525854f1ea1075f0731437aaa
ACR-6cb4ba8b847147a19a4e8dfa0a65e42f
ACR-4b75c2524ef744d88ea96aaed9cf01b3
ACR-10f98b11ef0a47b5a53f5a522ad4ce60
ACR-15015405daed42a6b4f452af25768b42
ACR-fb2b8f625bde42999cad038641eb1583
ACR-53dcf0fe58ff4f89a87aaec65498e0d2
ACR-f59c24215e7c42af80cf15fe1db4d825
ACR-026c7ec19dea474d9c2f08b5261dc81f
ACR-54846773c08b411281fb9926ecc4c0c7
ACR-2b2880084f564fbd95d47d481f2e3d87
ACR-100d97161f634d468f1ead1a92d504e9
ACR-b901fd47104b487fbbdae317ce9e1657
ACR-b49953648a804865b2637542ec1c5947
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.sonarsource.sonarlint.core.telemetry.TelemetryUtils.isGracePeriodElapsedAndDayChanged;

class TelemetryUtilsTests {
  @Test
  void dayChanged_should_return_true_for_null() {
    assertThat(isGracePeriodElapsedAndDayChanged(null)).isTrue();
  }

  @Test
  void dayChanged_should_return_true_if_older() {
    assertThat(isGracePeriodElapsedAndDayChanged(LocalDate.now().minusDays(1))).isTrue();
  }

  @Test
  void should_create_telemetry_performance_payload() {
    Map<String, TelemetryAnalyzerPerformance> analyzers = new HashMap<>();
    var perf = new TelemetryAnalyzerPerformance();
    perf.registerAnalysis(10);
    perf.registerAnalysis(500);
    perf.registerAnalysis(500);

    analyzers.put("java", perf);
    var payload = TelemetryUtils.toPayload(analyzers);
    assertThat(payload).hasSize(1);
    assertThat(payload[0].language()).isEqualTo("java");
    assertThat(payload[0].distribution()).containsOnly(
      entry("0-300", new BigDecimal("33.33")),
      entry("300-500", new BigDecimal("0.00")),
      entry("500-1000", new BigDecimal("66.67")),
      entry("1000-2000", new BigDecimal("0.00")),
      entry("2000-4000", new BigDecimal("0.00")),
      entry("4000+", new BigDecimal("0.00")));
  }

  @Test
  void dayChanged_should_return_false_if_same() {
    assertThat(isGracePeriodElapsedAndDayChanged(LocalDate.now())).isFalse();
  }

  @Test
  void dayChanged_with_hours_should_return_true_for_null() {
    assertThat(TelemetryUtils.isGracePeriodElapsedAndDayChanged(null, 1)).isTrue();
  }

  @Test
  void dayChanged_with_hours_should_return_false_if_day_same() {
    assertThat(TelemetryUtils.isGracePeriodElapsedAndDayChanged(LocalDateTime.now(), 100)).isFalse();
  }

  @Test
  void create_analyzer_performance_payload() {
    var perf = new TelemetryAnalyzerPerformance();
    for (var i = 0; i < 10; i++) {
      perf.registerAnalysis(1000);
    }
    for (var i = 0; i < 20; i++) {
      perf.registerAnalysis(2000);
    }
    for (var i = 0; i < 20; i++) {
      perf.registerAnalysis(200);
    }
    assertThat(perf.analysisCount()).isEqualTo(50);
    var payload = TelemetryUtils.toPayload(Collections.singletonMap("java", perf));
    assertThat(payload).hasSize(1);
    assertThat(payload[0].language()).isEqualTo("java");
    assertThat(payload[0].distribution()).containsExactly(
      entry("0-300", new BigDecimal("40.00")),
      entry("300-500", new BigDecimal("0.00")),
      entry("500-1000", new BigDecimal("0.00")),
      entry("1000-2000", new BigDecimal("20.00")),
      entry("2000-4000", new BigDecimal("40.00")),
      entry("4000+", new BigDecimal("0.00")));

  }

  @Test
  void dayChanged_with_hours_should_return_false_if_different_day_but_within_hours() {
    var date = LocalDateTime.now().minusDays(1);
    var hours = date.until(LocalDateTime.now(), ChronoUnit.HOURS);
    assertThat(TelemetryUtils.isGracePeriodElapsedAndDayChanged(date, hours + 1)).isFalse();
  }

  @Test
  void dayChanged_with_hours_should_return_true_if_different_day_and_beyond_hours() {
    var date = LocalDateTime.now().minusDays(1);
    var hours = date.until(LocalDateTime.now(), ChronoUnit.HOURS);
    assertThat(TelemetryUtils.isGracePeriodElapsedAndDayChanged(date, hours)).isTrue();
  }

  @Test
  void should_create_telemetry_fixSuggestions_payload() {
    var suggestionId1 = UUID.randomUUID().toString();
    var counter1 = new TelemetryFixSuggestionReceivedCounter(AiSuggestionSource.SONARCLOUD, 4, true);

    var suggestionId2 = UUID.randomUUID().toString();
    var counter2 = new TelemetryFixSuggestionReceivedCounter(AiSuggestionSource.SONARCLOUD, 2, true);

    var suggestionId3 = UUID.randomUUID().toString();
    var counter3 = new TelemetryFixSuggestionReceivedCounter(AiSuggestionSource.SONARCLOUD, 1, false);

    var fixSuggestionReceivedCounter = Map.of(
      suggestionId1, counter1,
      suggestionId2, counter2,
      suggestionId3, counter3
    );
    var fixSuggestionResolvedStatus1 = new TelemetryFixSuggestionResolvedStatus(FixSuggestionStatus.ACCEPTED, 0);
    var fixSuggestionResolvedStatus2 = new TelemetryFixSuggestionResolvedStatus(FixSuggestionStatus.ACCEPTED, 1);
    var fixSuggestionResolvedStatus3 = new TelemetryFixSuggestionResolvedStatus(FixSuggestionStatus.DECLINED, null);
    var fixSuggestionResolved = Map.of(suggestionId1, List.of(fixSuggestionResolvedStatus1, fixSuggestionResolvedStatus2),
      suggestionId3, List.of(fixSuggestionResolvedStatus3));

    var result = TelemetryUtils.toFixSuggestionResolvedPayload(fixSuggestionReceivedCounter, fixSuggestionResolved);

    assertThat(result).hasSize(3);
    var resultingSuggestion1 = Arrays.stream(result).filter(s -> s.suggestionId().equals(suggestionId1)).findFirst().orElseThrow();
    assertThat(resultingSuggestion1.suggestionId()).isEqualTo(suggestionId1);
    assertThat(resultingSuggestion1.aiFixSuggestionProvider()).isEqualTo(AiSuggestionSource.SONARCLOUD);
    assertThat(resultingSuggestion1.countSnippets()).isEqualTo(4);
    assertThat(resultingSuggestion1.snippets()).hasSize(2);
    assertThat(resultingSuggestion1.wasAiFixSuggestionGeneratedFromIde()).isTrue();

    var resultingSuggestion2 = Arrays.stream(result).filter(s -> s.suggestionId().equals(suggestionId2)).findFirst().orElseThrow();
    assertThat(resultingSuggestion2.suggestionId()).isEqualTo(suggestionId2);
    assertThat(resultingSuggestion2.aiFixSuggestionProvider()).isEqualTo(AiSuggestionSource.SONARCLOUD);
    assertThat(resultingSuggestion2.countSnippets()).isEqualTo(2);
    assertThat(resultingSuggestion2.snippets()).hasSize(1);
    assertThat(resultingSuggestion2.snippets().get(0).status()).isNull();
    assertThat(resultingSuggestion2.snippets().get(0).snippetIndex()).isNull();
    assertThat(resultingSuggestion2.wasAiFixSuggestionGeneratedFromIde()).isTrue();

    var resultingSuggestion3 = Arrays.stream(result).filter(s -> s.suggestionId().equals(suggestionId3)).findFirst().orElseThrow();
    assertThat(resultingSuggestion3.suggestionId()).isEqualTo(suggestionId3);
    assertThat(resultingSuggestion3.aiFixSuggestionProvider()).isEqualTo(AiSuggestionSource.SONARCLOUD);
    assertThat(resultingSuggestion3.countSnippets()).isEqualTo(1);
    assertThat(resultingSuggestion3.snippets()).hasSize(1);
    var telemetryFixSuggestionResolvedPayload3 = resultingSuggestion3.snippets().get(0);
    assertThat(telemetryFixSuggestionResolvedPayload3.snippetIndex()).isNull();
    assertThat(telemetryFixSuggestionResolvedPayload3.status()).isEqualTo(FixSuggestionStatus.DECLINED);
    assertThat(resultingSuggestion3.wasAiFixSuggestionGeneratedFromIde()).isFalse();
  }
}
