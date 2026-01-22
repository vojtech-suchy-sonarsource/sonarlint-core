/*
ACR-c897d6dcac78432d982038cd2a6a9836
ACR-829472a3b0d54c19b34532a27cef3971
ACR-3760ef5f4239488aaedb5e6f5b32e3a6
ACR-96b33b6908e84d40aa2da179cb713926
ACR-274519c90bf3439cb20a3cd350e65226
ACR-d7bb3ee8ad614e709e0b7245e5cc852e
ACR-960d60433867485b8331c60ebf0f1fdd
ACR-c3049dbe6fa4467fb2e9d9c53a7c669d
ACR-56bc87cab9924d7198a0ae6e89d9fc02
ACR-a57b747c16e14791a5b2f7b4c3bba15d
ACR-ba0d6de8fd3241268b9aa79898fc8789
ACR-d5ecfbbe97ad4156957b3e43296dff01
ACR-e4d7e17396e047ceaae913278b1f0f05
ACR-ca05d6c5fbe441b6804df6a0118b5e50
ACR-d323a9de8cec43bf8281a5de4366fce9
ACR-233e0bf305d64a409a2f4527cf10e755
ACR-93b3a3a7a95043b186f4125436308b6a
 */
package org.sonarsource.sonarlint.core.telemetry;

import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;

public record TelemetryFixSuggestionReceivedCounter(AiSuggestionSource aiSuggestionsSource,
                                                    int snippetsCount,
                                                    boolean wasGeneratedFromIde) {
}
