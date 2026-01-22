/*
ACR-a6a8b20d719b4f71a057dceca055f690
ACR-56c0f75052124e64b71b9d44e532da0e
ACR-2d21de3e0f8c48f88f86359a9aef46b3
ACR-6d8fb59679b3417a9e6e953c2efdf5ca
ACR-ff3bd2a3d8514c3f8d42fe72194d0fac
ACR-6cce24d080954619a0deda778ce99558
ACR-c42026b3324543fa8a4dade85a470782
ACR-af165c775b5442afa9d031f59c1be5f6
ACR-149d27181d4c4b0bb21f9bcfb32f8dd8
ACR-7ec610c42f6347d68162473c5877befe
ACR-b1c27294b3d44e9382b3cd0e14ef8146
ACR-e393c60efdeb4acf9858da35c19675ad
ACR-52b960d88c2c4eb893dff053366da580
ACR-e7a4839ac9c740ec87774f6ad1933acc
ACR-0cadccb6c6c04aefb63ccba16bc46843
ACR-5abd6cc4e60042ad996568eba6b3b338
ACR-75cd367c52054c19b9cc454029ba395d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherRuleDescriptionTabContentAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class RuleDescriptionTabDto {
  private final String title;

  @JsonAdapter(EitherRuleDescriptionTabContentAdapterFactory.class)
  private final Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> content;

  public RuleDescriptionTabDto(String title, Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> content) {
    this.title = title;
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> getContent() {
    return content;
  }
}
