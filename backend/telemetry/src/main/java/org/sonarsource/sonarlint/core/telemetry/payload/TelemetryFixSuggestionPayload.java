/*
ACR-77c76ca0685e4d53adef515ad9cc10c3
ACR-e7b078e769244e308bfacf276dc3ea89
ACR-78492d4d8c9a492ba04f076c295ae414
ACR-d542f94d17c340a391d01da2a91f366e
ACR-6e54c6cc03ef42349bf8e4cc1aac08c0
ACR-7ddcdf107a434d45be5965483913b467
ACR-2762bc29746c46d986dda6ec7dfe1628
ACR-65450a5a6f8a4465bc29a3ad00649001
ACR-59b6d042224747caaaad7bcf6ff25ff3
ACR-604e563f545f47659e33d319d14ad410
ACR-b0ee70a6e60e4e1ead444d15680b51ac
ACR-d278e7c9d4a64f9696dc0ff3e70f223d
ACR-bb3eaf793521412b9351cbd23682c4e0
ACR-715545091a56441483c38202e966af20
ACR-230cea4c1df44e4b9857fb23007bcbdf
ACR-ab948105d9dd4ddd9b06b7f9620640d9
ACR-ad92b6ee6dce46f4ad3ddf3e16303259
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
