/*
ACR-d2636a23367f49e8927a69f968d43cb2
ACR-57fa230d3c8449e7984aed09e2f7383f
ACR-6e0ed88cd04747e5965f700ccb46ef8e
ACR-a381b6479cd34d4bb99b1218efda1f6a
ACR-6cc3162fec29457eab6da793e47017bf
ACR-3dad58a5bf184ff1bbf5cedf08b82383
ACR-9791fc1222cd4fcfa3ee3c75aa72feba
ACR-41064c07ca5045e2b98203d535bc143f
ACR-feb42c568dbd4bcf888f275e60dc10e6
ACR-8ae5b37e10714bbf9fe8bd954fc8cea4
ACR-4fb97f9a60ef437e99084e251be99a2a
ACR-446e3253110c4b8faadc0f487b52acc0
ACR-a2e16c390f034b739ad322f8d290f432
ACR-c72f1458ac714fc883d36bc3e3f590c1
ACR-cf39b76ebd4943e4a2d355fbcba9eff2
ACR-5d62d4b3eec84202930c45ea1e0d9f68
ACR-fdd554a54f4c4befbba817bf746f65e6
 */
package org.sonarsource.sonarlint.core.analysis.api;

import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class TextEdit {
  private final TextRange range;
  private final String newText;

  public TextEdit(TextRange range, String newText) {
    this.range = range;
    this.newText = newText;
  }

  public TextRange range() {
    return range;
  }

  public String newText() {
    return newText;
  }
}
