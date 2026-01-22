/*
ACR-180dd0833510458b8d3d9a0a371700fe
ACR-e424717eb1014f5c8603f34af097382c
ACR-03370cfa737149d8a562a1186d8c2fad
ACR-d28d9f16f8464cc0befcab69a07c13cd
ACR-cec2cfe7e3fc4e8dba6011ac0a7de669
ACR-e375afa202784de29a43e27e1b6f0ffe
ACR-6fcd964c72ae488aa6a3a72fb72c6dcf
ACR-dc5e9434e00e4592b39bb6082bff4f8c
ACR-cc88917eea1b496d9cc7e7d579dc7786
ACR-bc30b47e35384e4eb977eb070bbaee52
ACR-35b1ec5054794a859647993bfc08d106
ACR-123249e503d5414aafd2663dc245cf34
ACR-f96dee375a36417f96c44d5c4fee1a6e
ACR-e1ff3996394b415bb4f40523954f52b0
ACR-527eb19c4339478d90449258018195e8
ACR-9a5023244b7e4309b103c106d8315036
ACR-db4be40bd12640d69240b291ae8fa040
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
