/*
ACR-95f72c707c814b6aaa0acd701cecbd4c
ACR-b4e85f3658e54c72bbc56489b1606416
ACR-4193961213a440e0be8fb736f99c0c9b
ACR-fd4337f04ecb4441a3cda4a0a0274463
ACR-d8bac0564d444351a7f76d1ae65508d5
ACR-a9b712a6c9cc49f7a0531ee74650991e
ACR-6520dc8a42244bd7898a2ef6d118eef8
ACR-16b586d9fd354241a7025fb0d36e41bd
ACR-71babae2a431420ea9b908a9cc195407
ACR-fb3e356050354636a25556a2e312f44e
ACR-c6e9cd8fc8a94a27a710c12508f5bdbc
ACR-a9391da2cc6249a19851072da6ccaae5
ACR-3277c48a63a6433d93ab9e2b07c8a168
ACR-c42746d065ed4e2da4bc7ebf89c7dcf6
ACR-7adc8e527da542fc927794add6681e9b
ACR-4eb0fd1e52494eaaa5c7303d47ebc4c4
ACR-53d355e942f04bd0a93a5a2ec1ac6eb3
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Optional;
import org.sonar.api.config.Configuration;

public class EmptyConfiguration implements Configuration {
  @Override
  public Optional<String> get(String key) {
    return Optional.empty();
  }

  @Override
  public boolean hasKey(String key) {
    return false;
  }

  @Override
  public String[] getStringArray(String key) {
    return new String[0];
  }
}
