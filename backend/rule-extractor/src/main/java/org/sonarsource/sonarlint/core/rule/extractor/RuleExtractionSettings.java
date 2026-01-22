/*
ACR-1e25e900a80640a985447af16678a9fb
ACR-e1ef484163d443faa40fda57cc85e520
ACR-a656d098044840119dd197d228daf67c
ACR-36cd23a3c26a46329b715dd91f742332
ACR-076a02094111479b8e86db48fa91afa7
ACR-52e4cac030c941078e45b8cd4cf02237
ACR-3ec7f21ecd4b49659d0c4d7bfb878fa5
ACR-2d6922b0745847d4a97c7e1d6916fbef
ACR-0e221050bb584914914787cf49668e50
ACR-a4034fd97b2d4dd192797d6675039199
ACR-32ec5cf3cabb4cceb80eeb9ec3f65f09
ACR-bc8fe90a442148d188e9b815a4550ed7
ACR-b4d2216791f14f7d8310c82346af74ec
ACR-57606c79fb504156add8b6068fc07fb6
ACR-ddec3ad278bb4f118e42f259536b754a
ACR-670bbf84a1274d9f9c006cbb0fb837f8
ACR-58781b86f1cc4c1a88851eb956f7cd62
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.sonar.api.config.PropertyDefinitions;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

public class RuleExtractionSettings extends MapSettings {
  public RuleExtractionSettings(PropertyDefinitions definitions, RuleSettings settings) {
    super(definitions, settings.settings());
  }
}
