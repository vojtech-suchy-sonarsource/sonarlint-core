/*
ACR-3f603bdda30f41c8b306307b44f0f3d3
ACR-57dc14e158044f379f76346580c0b729
ACR-167c567d30c34c38a9518ddd374df48c
ACR-47e59e6f1a8f4cd1aab9feb414105239
ACR-65ce73bfa1cd4307b15369551b9e5150
ACR-da22950bbf6a49df9487d2f38119a457
ACR-9c89edef39e64c68ac10996b00857c00
ACR-0f7dc220fd0747efa89477fbde0bfab1
ACR-5ae6b49c97fa480abf621f2f035b501e
ACR-9b99863d853e428bb83c38f33585a917
ACR-20e292f5035b4411a75a0ac5042b9f81
ACR-c3c7610da28f4cf8ab63749ecbcc1e7c
ACR-b9bae179e11746d49ab7555db2b92ee4
ACR-155b9f5fa20640c286b98cdd091adb7f
ACR-481cc860522e4c798058705c388ac945
ACR-e06c5979a16e46bbad6bafe76bf29547
ACR-e18627554a19493580868c136d0f0837
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
