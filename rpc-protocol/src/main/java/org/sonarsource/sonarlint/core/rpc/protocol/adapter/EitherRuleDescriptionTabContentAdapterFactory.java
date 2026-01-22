/*
ACR-ebb33579c99047eb8c91c4b0b337eb85
ACR-7acb879b65d241b49cb8cdbd933c8c1b
ACR-9161877adbec400bac91b440b9ab0c45
ACR-2d6a47a68ad04076a64f47529083f31f
ACR-c4a77e793309402a811ecbaccec39e94
ACR-a52bb807bb0e4f5dbf596759e3ab29a4
ACR-0eebc2297915449fbe5c58b1dfabb648
ACR-ac1480d2eb644fbb968dc66bf491cc98
ACR-ca1c1001b4cf41399fe59071ff23be02
ACR-7bd9c4d511594f909ce6ef672a44b74c
ACR-63374f297c6d44c8b2204a0d7d94fb8d
ACR-5e05b20265d54ad89e5c5a9452a0b050
ACR-8d63216fe70242f9b86fec0145428845
ACR-269a4dc4c2af4ac9bb0444faa84900c0
ACR-ffc23d9a15314a65acffaed6309b1ccb
ACR-9f9c991e97884a4f81272beb4cdab323
ACR-4b7eb4ee873b4af1b823bd014eafce6a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleContextualSectionWithDefaultContextKeyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleNonContextualSectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherRuleDescriptionTabContentAdapterFactory extends CustomEitherAdapterFactory<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> {

  private static final TypeToken<Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherRuleDescriptionTabContentAdapterFactory() {
    super(ELEMENT_TYPE, RuleNonContextualSectionDto.class, RuleContextualSectionWithDefaultContextKeyDto.class, new EitherTypeAdapter.PropertyChecker("htmlContent"));
  }

}
