/*
ACR-558623f395664914843f4feb3e07aa96
ACR-e74a6178a9544e02a351ea70f2390386
ACR-7c7f4fdf362c4f3f979d8531639eaf02
ACR-9af5526885f8478caae6fda7c6115ffe
ACR-7c8f610aa1414863a10681e375b18183
ACR-c9bb942a797147b2a357324f50051127
ACR-4e186b68f20f4d3f8b1db6cc36d7377f
ACR-435a11398aa748c99b9d899310abeb6f
ACR-4689ff752d334f3d8c6afa35f79c72aa
ACR-f9bae2254e4c444d86befc0f036a089e
ACR-e700e7d906a242978867f0bc8260c081
ACR-0c19440697714816a505cc228111df6f
ACR-8255598226ea477fbbc7a85cdc983494
ACR-525bca8f77f442ce90bdf8adb9a35d5f
ACR-32ffbdc69ec24980b7fc26fa981c918f
ACR-c43951a2bdbb417693eedf0e0cd43c7f
ACR-d76d05de0a1b4390be3c353986592111
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
