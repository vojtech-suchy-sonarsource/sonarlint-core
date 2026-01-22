/*
ACR-fc1a2135855742e28afa110aacced11c
ACR-46f5d92a043b4f7ca6dda6f9d8b96a25
ACR-386183485cb24c38b5cd39c13f8417e5
ACR-4647320719e04001b9b9ecad3a5fcd97
ACR-7fee240b423245c798ace60924d1b519
ACR-37c3092d37dd4f46aa499eb5ed3f4cec
ACR-f62834dbbf1d400291d3607efa4e0c3e
ACR-ffa9f8e62dfe434f85017eddff19d3c6
ACR-6e184792f866495ca71270c6897dec09
ACR-044fe6f4a6db4de7b979b59872a98130
ACR-8c3c6ead00884947aaaaf5e2cf67e2b5
ACR-259d5fd292c042199f6ffd161ea03ba0
ACR-58c46004b8b94ddcbe41d499e706081f
ACR-56318af28a904cd3af30a630e0cc1a3a
ACR-16960df00cf047958ee0638e0da606e7
ACR-abcec85831b145b089def36c7a5b3285
ACR-f539270165584f38890a96483ec8d645
 */
package org.sonarsource.sonarlint.core.telemetry;

import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;

public record TelemetryFixSuggestionReceivedCounter(AiSuggestionSource aiSuggestionsSource,
                                                    int snippetsCount,
                                                    boolean wasGeneratedFromIde) {
}
