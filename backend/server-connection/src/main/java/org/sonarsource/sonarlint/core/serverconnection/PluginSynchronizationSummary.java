/*
ACR-4114b9b231b44721ae1dac2babedebc1
ACR-975efbe514e9460a9d24520eb4bb78a3
ACR-df19b7c5e4e84e4cbf3d1d08d91f2f35
ACR-8884feb5a81f454e829927608d965508
ACR-c4d054a4a7644e108fc1cec31a533cb0
ACR-8c60845aadcd4049a9cf535ed140c9c1
ACR-f3f3b243c0f74db3bbe73e3954260752
ACR-1b048f4fddcd4a55b9f3af4cfe05f8da
ACR-099ee7a1f0474429a4234b503a36cff8
ACR-a3ee52c054ec4d1fab468a6c5ee0adb1
ACR-71a1de7125d1452ea8a4f49281cbf914
ACR-c21cec797ac744a8b1afccf7bd504e8a
ACR-c6e0b818f6e64befb3cf558c54b28b3a
ACR-eec6ff55dc6c40f4afb143b2662843a2
ACR-54095340314947ba9a5782e82a9817d0
ACR-b6cb4b045d6047d28978810bf7f4b29b
ACR-c76edceaeb604ff5a80269063f4bde62
 */
package org.sonarsource.sonarlint.core.serverconnection;

public class PluginSynchronizationSummary {
  private final boolean anyPluginSynchronized;

  public PluginSynchronizationSummary(boolean anyPluginSynchronized) {
    this.anyPluginSynchronized = anyPluginSynchronized;
  }

  public boolean anyPluginSynchronized() {
    return anyPluginSynchronized;
  }
}
