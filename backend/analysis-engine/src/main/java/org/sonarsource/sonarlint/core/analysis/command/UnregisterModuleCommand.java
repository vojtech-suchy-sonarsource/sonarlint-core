/*
ACR-29cfa888d5594ddc9bbcf13969eec00b
ACR-fd7198c8aa8f497dbf1d5a2ff03b114b
ACR-d8f41a652cda4455b6fde65994b1537c
ACR-7f0a07e246704f74813454083c15db08
ACR-33bbd760283f49f2bc6e9d9d0c2fdc29
ACR-d4572b9722414202b428c4736391f82f
ACR-92ed29158ec14df7a346e2d8a1c018ed
ACR-1d50ac4022cc469188f904328cca27eb
ACR-f96585433a8c478882b94357ed930a13
ACR-4d89fe72c6ea4a018b30b9fb5d934381
ACR-3528636006034c9e8e29bc4a7e97175d
ACR-85c06cab805646e0a4b2036dd15af5f6
ACR-1f3374ad9055462596c303d2a866e755
ACR-4d07632db088424098f25e0a23844f7f
ACR-07203db218084facab3ea7389275cd39
ACR-4ce80aa69d7843559fe5a70aa0c810ef
ACR-a5d9438e12e645e89456cffaeebeb2f5
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
