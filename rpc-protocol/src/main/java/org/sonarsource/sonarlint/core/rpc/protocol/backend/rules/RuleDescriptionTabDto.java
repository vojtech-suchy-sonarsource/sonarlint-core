/*
ACR-7660ca5827ce4b18895ca3ac9b44e29e
ACR-659739d7c63a4ede815ed230c36079db
ACR-b834431b9fc042459fecb7f2b99e744b
ACR-26ea013fc76f47db91355c34bd20179d
ACR-7eedb6cf9bac472b900565f8b1346c05
ACR-c47315704a874e1c809cb4dc9b72138e
ACR-7a6e010fac8f417fb413a9ed2f3cb5c5
ACR-88205fa22114423b8c95c23ad63f8910
ACR-5ea416c3cd5f427cad6425edc92073bc
ACR-5d3b0b4876db4ba199c6d76cdfdbf457
ACR-06c4140bc04842128371eb0fcf7e574a
ACR-38e3cf2cad5748a5ad1dd01246cb3c21
ACR-b0a32f4bccaf4ab9937dbf525884b227
ACR-b40166162be645f2bc13f900e4910e92
ACR-d6bdf8ce976d42828656642c49bcf8bd
ACR-064e2704241b44bca367f613e69cf337
ACR-4ac3408b1c4843648936ba0268510f2c
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
