/*
ACR-53e843befd62401ea36360fca53cf800
ACR-f1e35a58d6524c548eae85d300a90bfa
ACR-3be5e9f5cd14498a801c217526354649
ACR-fa9439b7ed824226b832b6eb501c07b2
ACR-f0a880185e8b4fde98b336bf5b454b1f
ACR-12b7fb32251a4f9bb7968acf2b0a9b13
ACR-7b1880816d1c47c99345fbc53fa7ca65
ACR-c4e5ed41566a4709bf49f86ea6c17321
ACR-7b11e50d2f684e65b292ebac0ad15096
ACR-ba0f8e7e78184694957f7eaa8089d159
ACR-3db7cfbb466243b88e42ca2f94213fff
ACR-62a2fbd9243f4b2e8362429f74d4462b
ACR-d6a3696fb7ee4e8796e7f5c636758753
ACR-7c89b1b1c5a648ea8ac27487244d3a2e
ACR-99560a5882a34f82ba3a8ed78ad842c4
ACR-e0df7ad790284a759d2c7b501efe0945
ACR-b88e2850d5cd4a67b897023a0cc96bc9
 */
package org.sonarsource.sonarlint.core.commons.validation;

import java.util.Map;
import java.util.regex.Pattern;

public class RegexpValidator {

  private final Pattern pattern;

  public RegexpValidator(String regexp) {
    this.pattern = Pattern.compile(regexp);
  }

  public InvalidFields validateAll(Map<String, String> namedValues) {
    var invalidFields = new InvalidFields();
    namedValues.entrySet().stream()
      .filter(this::isInvalid)
      .map(Map.Entry::getKey)
      .forEach(invalidFields::add);
    return invalidFields;
  }

  private boolean isInvalid(Map.Entry<String, String> nameValue) {
    return !pattern.matcher(nameValue.getValue()).matches();
  }
}
