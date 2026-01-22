/*
ACR-4d47075f79a946f4a893849051b9777c
ACR-5ba9dfc557574d248b14da47a68ec45d
ACR-8e44ebdbe4d44bd6bf20349457a5ec21
ACR-1ff3be9f0f45460e8af84c685e8a5c3c
ACR-42d191a3011241aa865a7a16f848993a
ACR-de60492df074456b9d6351217cb91301
ACR-c71b97933b3644d58c8510ee0a233889
ACR-fe881206b6e749cf8a3f48af92033274
ACR-53190c49b58a4bf59f0231d8dea4e263
ACR-978045ea608c4a4bb07e94de908798c6
ACR-9619ff2da5144834b66e62ce1b440748
ACR-7748f308866e43aeb9aeb306901f9d12
ACR-8937fb084e9143739f6827f36bac1988
ACR-ac9545409cd14666b58b37a7946b5a8f
ACR-0dbe272a670f4bb7a9b4e4f4877aad6f
ACR-d750f5fd0a2942fbb18c357773123b72
ACR-f14541e3852d413aa59956c6b7f51148
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class RuleNonContextualSectionDto {
  private final String htmlContent;

  public RuleNonContextualSectionDto(String htmlContent) {
    this.htmlContent = htmlContent;
  }

  public String getHtmlContent() {
    return htmlContent;
  }
}
