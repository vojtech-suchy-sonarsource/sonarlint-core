/*
ACR-be0b9f69c340496aafa52fd4c507df2b
ACR-37d67e8af4ee423db3349e9b712c8c69
ACR-b78632716ceb40f2857a9535caf5dd9e
ACR-56eab1af264f4ebf97787a83c89345dd
ACR-b3d41ad4028044b182fd47a0ac1370de
ACR-a869ecb76c5f4b4d9e67dd423bd37dbb
ACR-a3440d44126e48c0ad223e50ecae03cf
ACR-5efda2d89f8a41fcb5ab00dab406e154
ACR-65e738ebcc5142e2bf457fcebf7c7b6f
ACR-2020f6b156ff4c5d86392eac1128b820
ACR-47a5fce66c8844b59588913540e9c936
ACR-5fe8ecd8082d4ffb9df07f682fe9f832
ACR-d89eec5933014b85bf8462321a20f1ee
ACR-3da70e2190c4403c8750368e09640ca6
ACR-77b54998fd944ebdb91803f55b6c4cbe
ACR-047941c813b940abb625268cee8eb765
ACR-ff278fd4e8a940d395f6c59da8359b87
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.Arrays;
import java.util.Collection;

public final class Language {

  private final String key;
  private final String name;
  private final String[] fileSuffixes;

  public Language(String key, String name, String... fileSuffixes) {
    this.key = key;
    this.name = name;
    this.fileSuffixes = fileSuffixes;
  }

  /*ACR-6eda90e82ccc453f8fb30c152513140d
ACR-5143d80b3b8e441294ae3b6ac66484ac
   */
  public String key() {
    return key;
  }

  /*ACR-abfdc9eeaa334e59b0aa86b5dc1f9196
ACR-43d30d6a4f454683b874f2a321cae612
   */
  public String name() {
    return name;
  }

  /*ACR-eb5d7a8c67e74e4fa8bbb1555e5f4a3a
ACR-f847dbabf54a475ba9d59cc8b204b9d9
   */
  public Collection<String> fileSuffixes() {
    return Arrays.asList(fileSuffixes);
  }

  @Override
  public String toString() {
    return name;
  }

}
