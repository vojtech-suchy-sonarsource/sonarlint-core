/*
ACR-24694c6186e346c591104fdba8332388
ACR-8cbf6375cfb84b9d8e9e34b95baefc78
ACR-5d96816134da483aba773a4d3a0be4b6
ACR-cf25ab947f6d42c5bbb5442764ddd3b6
ACR-ff9876d06332436baa88c3d4071a3063
ACR-91a5862e0ca149af9c2e1e460d1dbc3d
ACR-67b6fecc41474d57a0289afa55449882
ACR-c46d1277277e4353a4c7f9c3c241a5e0
ACR-0573ee47fb4140e5bcc04a2a1d3db544
ACR-485d96f66c7847a899a5d9f4f5c67923
ACR-d13edfd37d744b5b8e9349e8dd0e8e3f
ACR-f654df1bca804da1bc3f1e4811824fe8
ACR-99e7c9ab8f764940b9021f1d23fd1c83
ACR-29d0ecf4cb3345a88075aa6ed9a9f7bf
ACR-4b2ab9b22ded4612afbb07fce120bb97
ACR-4b48c934aaf34a73a547044f79a49927
ACR-712f4ee024a0492fa93397d8fb66b756
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.List;
import javax.annotation.Nullable;

/*ACR-6d5dd59fc2cc46c1b3ee0991cde48ab7
ACR-f5cb1d16b25a4b65a476953c2f167eb5
ACR-76867a443dfd49f4a533f98752a0b49d
ACR-4cc118732d16475c9ff5ae7083dc071d
ACR-aa95075ccb414063ab95df86771ed487
ACR-6eba7020da454e82b9721e1d6a6a9ea3
ACR-2dfbbbf6d67e42ccba68c5b8eab5346b
ACR-655006598c1445bb8081718692a2f160
ACR-eac1206fe8c94baa8282a83ad0d0a714
ACR-03a99e5971714246a1a04e5dcd4e9240
ACR-414bfa6b7d984af3ac079672938cac6e
ACR-90ec2cc8e4d346758aba9fe06a431360
ACR-9b7d26f3560e4d5aaf6eb5e09df38286
 */
public record TelemetryServerAttributes(boolean usesConnectedMode, boolean usesSonarCloud, int childBindingCount, int sonarQubeServerBindingCount,
                                        int sonarQubeCloudEUBindingCount, int sonarQubeCloudUSBindingCount, boolean devNotificationsDisabled,
                                        List<String> nonDefaultEnabledRules, List<String> defaultDisabledRules,
                                        @Nullable String nodeVersion, List<TelemetryConnectionAttributes> connectionsAttributes) {
}
