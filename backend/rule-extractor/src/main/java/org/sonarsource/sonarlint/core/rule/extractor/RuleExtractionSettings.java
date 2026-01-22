/*
ACR-0ebd2ae6dd384baf861fa35070d0ddf8
ACR-5566c8ffe0674287a7fcdb05ac27c36d
ACR-2ad829d3c696439f8750479412dfbc9b
ACR-15261826208e40f7b138c9fa4586f177
ACR-9ea6cea2e3a5435f8537c3068bae2b8b
ACR-2798f4e6bdda4287833f70391c267232
ACR-67ef02a2356d4140b71b991395c2523a
ACR-d47c3a9b15634517af0f64cdb52e451a
ACR-e1605f8bd4f54fb3871cec24b4020ba2
ACR-287fed28f8d645109360f686ed2d9e5e
ACR-bbfefc98e4814328bc1b3716766d16e4
ACR-1fe8b50f69154885ae97e0d8c794e475
ACR-88f2a981ab6047ba929411fc7fac900a
ACR-2789b83c064b4b1aafe2ca6be311a913
ACR-f3407761007b48fd8a9299f5c06e0c90
ACR-02dc35b6e60342d491bca7074b665edb
ACR-f23f69a1802b411f86da041e0040fd49
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.sonar.api.config.PropertyDefinitions;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

public class RuleExtractionSettings extends MapSettings {
  public RuleExtractionSettings(PropertyDefinitions definitions, RuleSettings settings) {
    super(definitions, settings.settings());
  }
}
