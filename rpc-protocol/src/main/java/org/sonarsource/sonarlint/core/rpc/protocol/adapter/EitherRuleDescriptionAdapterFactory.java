/*
ACR-ed645c3b8d774a0da3f92db759409aec
ACR-dce419de773d43cbaa4640691b414dd6
ACR-f3617e64f4fb45d1860e1d4b8ce1b48c
ACR-f00f75f2e5804e7bb22a3ed6a108fb39
ACR-8783a0f28ab5401085621b2f576a9be1
ACR-ff1c7ad867034d9eb5be3b7b010dd28e
ACR-46e148e135c6404a9b40f3dcb85600eb
ACR-e971b40fc06f4b05a95c0fca6c3c1b4b
ACR-ed1de7cfef65413a851b4cf5998218b9
ACR-adcafcf34eaa4665a33069a9bc45e1e2
ACR-45fafb06d5194ce3bab6dd5c36fcea80
ACR-376f1c38e7f742e3b57e38634e46a53b
ACR-ebeeba9552af492d9be3d8a0ab5c6db0
ACR-6acb22821c044a31812b05ec6c68c69c
ACR-f8456810836b41e3985ff70061feb8be
ACR-84f2cd73d8614111a84018673fb1d8ed
ACR-3f6f9cfa29c74811bc2bb9843ddc0b1c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleMonolithicDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleSplitDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherRuleDescriptionAdapterFactory extends CustomEitherAdapterFactory<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> {

  private static final TypeToken<Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherRuleDescriptionAdapterFactory() {
    super(ELEMENT_TYPE, RuleMonolithicDescriptionDto.class, RuleSplitDescriptionDto.class, new EitherTypeAdapter.PropertyChecker("htmlContent"));
  }

}
