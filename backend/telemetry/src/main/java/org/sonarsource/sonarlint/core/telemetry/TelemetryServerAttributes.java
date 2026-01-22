/*
ACR-4c2ac72757c94973a58d170679faba6b
ACR-e2bdfd0ee21b4bbabc926988748767d9
ACR-545e180bd5f1451e89bf7f22969df846
ACR-a09a957727614095a667b659e3176437
ACR-37022230861e47b4a2f1d35d26e023cb
ACR-3cd2dea16a1845648b95b5345c4b719a
ACR-2d9bb6b3f6cb437db1668f09f0a3c2ab
ACR-8e85b4713db14f00929481c3e1dfa933
ACR-720729edd27d4dc695f79a2f63996a22
ACR-08b5a3279e7046869177fb6bc0130658
ACR-4ea2f4915ad14e6e803c7b8d3cbcdf29
ACR-640bec7754c84b78b794f2140b49e323
ACR-b9d4a00d4d284dffb8fa4ea971009856
ACR-2a6396e42ba14eccaf623b5bb9951767
ACR-b4395050450d4921b1a83342e5b40c78
ACR-e426551df84c405b917d6d50b6be82a4
ACR-d20f501f15444552abdee04e60cc3e3e
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.List;
import javax.annotation.Nullable;

/*ACR-266073c4ffe7434faad3b2e0af4293f7
ACR-f0bf387741ef48649f25f621ffea19a8
ACR-93e4749f4170420aa167b0747fc22493
ACR-399620eabfba415ab17750c7e2c7e373
ACR-436131ea4ffd457bbd7f4e96eca4a501
ACR-15ec35a09b944a039bedc0d3edafe2a7
ACR-8b58f89589ef4f40a22b60455bb8f6ae
ACR-599dc0fffd224bccb8b2d1d0123aa0dd
ACR-35a945900e0c49c6bf160bf12efaffad
ACR-511294f70b7c47eeb429e51fc4038a73
ACR-d4005afa4fca434990cd0c9d4c90f343
ACR-66426e788f364b86b84d2dd73afd9e71
ACR-490bca3ca83b482daf2387b07c3ab62a
 */
public record TelemetryServerAttributes(boolean usesConnectedMode, boolean usesSonarCloud, int childBindingCount, int sonarQubeServerBindingCount,
                                        int sonarQubeCloudEUBindingCount, int sonarQubeCloudUSBindingCount, boolean devNotificationsDisabled,
                                        List<String> nonDefaultEnabledRules, List<String> defaultDisabledRules,
                                        @Nullable String nodeVersion, List<TelemetryConnectionAttributes> connectionsAttributes) {
}
