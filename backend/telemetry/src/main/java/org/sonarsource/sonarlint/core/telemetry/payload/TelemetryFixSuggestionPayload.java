/*
ACR-6845f381b91840328791d42eb321b514
ACR-a94e8cb5b6674e0e82cbb6a131827b7d
ACR-17a47d74a4544cec86d972e7e71fe107
ACR-b8a197a5635b431dbf82ce25eeb1a0df
ACR-e7cda77411e7467da1d045334448221c
ACR-749ac5c8097246289c9cf9d7d651fe06
ACR-160a085afd9944918380e684549f357c
ACR-5ca9b31d950c485dac693997e7ce3f03
ACR-3e2b7884ce5a4f569e5b1f262e629b61
ACR-14bf4fc452544111ab26a8473b29eab5
ACR-516ee35e7c164cf7bdb0d3015157bf1b
ACR-f50ae100b74a46ddadb93636946d1276
ACR-9f87541b0acd480b82813739713ee13d
ACR-7cb4bceb5f324a5d8dfe2b5396a4e61a
ACR-a1e6f1bd762440668d85a40814f1f7f0
ACR-deba8c2fe6c541efa05b944223547a59
ACR-f6061ff5f3164bd8afc312b4a6c52ec9
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;

public record TelemetryFixSuggestionPayload(@SerializedName("suggestion_id") String suggestionId,
                                            @SerializedName("count_snippets") int countSnippets,
                                            @SerializedName("ai_fix_suggestion_provider") AiSuggestionSource aiFixSuggestionProvider,
                                            @SerializedName("snippets") List<TelemetryFixSuggestionResolvedPayload> snippets,
                                            @SerializedName("was_ai_fix_suggestion_generated_from_ide") boolean wasAiFixSuggestionGeneratedFromIde) {
}
