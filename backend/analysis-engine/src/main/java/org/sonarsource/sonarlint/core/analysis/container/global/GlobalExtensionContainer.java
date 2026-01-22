/*
ACR-349705f1097b4e4389db858f47c9a58b
ACR-b0fcf7ca6b6a40f4a7dc36c74423858f
ACR-8a6821cc64c04bb3ba789dd35416322d
ACR-5e125b8baa924ceba438269120b57078
ACR-5d5b800d692745c9a691bcad1bf18d9e
ACR-dd4017365ce34cdfa669eb246f849ee7
ACR-7aa59663719e432eb0797eb368c02b8b
ACR-6b724f56b4fb4bb2843453ae7c780d1b
ACR-1c3fca9d73454d41a6906e3330d02b46
ACR-6dc3a6ca7e2e49b6b4f4e5371ac30e83
ACR-24c6eab732ab43d8bbeb94d5d97d2f71
ACR-c18c77ab77b1478a9dc88e5663f9bb85
ACR-a43ba01e326a45bcb42536cb3ae22051
ACR-8e2ac10822bd45d3a22db195b1beacfc
ACR-2ab6787ce80146b4ab6afd93c3fac40b
ACR-626ec27c24f14f54bc7c5a922309364c
ACR-8a20bc48cc72491e9a19c670a086680b
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import org.sonarsource.sonarlint.core.analysis.container.ContainerLifespan;
import org.sonarsource.sonarlint.core.plugin.commons.container.SpringComponentContainer;

/*ACR-d20c6d1e18544571907fcfa5f50753d6
ACR-507a140853114832a38cd5b5d6b52e42
 */
public class GlobalExtensionContainer extends SpringComponentContainer {

  public GlobalExtensionContainer(SpringComponentContainer parent) {
    super(parent);
  }

  @Override
  protected void doBeforeStart() {
    getParent().getComponentByType(AnalysisExtensionInstaller.class).install(this, ContainerLifespan.INSTANCE);
  }

}
