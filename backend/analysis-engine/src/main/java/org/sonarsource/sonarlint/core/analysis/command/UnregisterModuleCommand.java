/*
ACR-1eb2657a086c492f887224a45a95a57c
ACR-3206fe8aeb8741739003349ca1c89b64
ACR-0d79e9e0bd3247c295678da0b5cdcab4
ACR-83fa550a622b46e0a64a67df11b99e23
ACR-83b4e37b3d6c4d2c8d926175cd5c1c00
ACR-78bcdd415223446fbb781cb3e1b68cb0
ACR-49e4b94544a34fa2b8d2f87c79a65fee
ACR-d32db881f6d046e6899f8b527491e929
ACR-233b5371f54c43dc8139ea4d158a2719
ACR-e49a610956494fa488067dc7bdaba50f
ACR-3cab1795ade64cf8a54c7bd7f479e60a
ACR-18586642f483453694ad694d474bedf9
ACR-ce19cac01c3849d28b0142a7f7850456
ACR-ade7adfe0212470f80d46442948c70f8
ACR-97c501803bcd4e79b0f64ed5f65e792e
ACR-a616ee5359cc425291a93d898a4e72ba
ACR-537b3ce02a3b4dd49a63fca1e02479c2
 */
package org.sonarsource.sonarlint.core.analysis.command;

import org.sonarsource.sonarlint.core.analysis.container.global.ModuleRegistry;

public final class UnregisterModuleCommand extends Command {
  private final String moduleKey;

  public UnregisterModuleCommand(String moduleKey) {
    this.moduleKey = moduleKey;
  }

  @Override
  public void execute(ModuleRegistry moduleRegistry) {
    moduleRegistry.unregisterModule(moduleKey);
  }

  public String getModuleKey() {
    return moduleKey;
  }

  @Override
  public boolean shouldCancelPost(Command executingCommand) {
    return executingCommand instanceof AnalyzeCommand analyzeCommand && analyzeCommand.getModuleKey().equals(moduleKey);
  }
}
