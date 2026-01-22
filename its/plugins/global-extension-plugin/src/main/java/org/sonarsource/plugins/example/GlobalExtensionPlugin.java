/*
ACR-ded80decac074732aeeb7aec8f68d934
ACR-073fdac76c5040eb83974f52a81df7c8
ACR-97f8f48767d54b5d8b25c57ca0586863
ACR-3c73669a43a746d38fe8682247eb3c1e
ACR-3c1dc8d4bd6848e78af35c942c6aa7a2
ACR-c1f35f678c024bf495ed54815f153c8e
ACR-253d24b1897b4535bbd801959c448ff3
ACR-823affc7a84e4c75bc10f66d7c971277
ACR-0149b5f23c554a8dae450e317f742c3c
ACR-5ebe4927cd47408b942982b02707c9ab
ACR-528addaedbe74073af948118cc37799d
ACR-21c2f0bbb2aa42a9a692a76b21c94003
ACR-56018594f0a14f62ab6507e7f9021123
ACR-41806b1eeb7f4b89bed66c4799056478
ACR-caef49568da140d28fa89cef86c521de
ACR-b633765971c34f92911579002bde96d0
ACR-62d506a6dae14416913eb0a467455c69
 */
package org.sonarsource.plugins.example;

import org.sonar.api.Plugin;

public class GlobalExtensionPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(GlobalLanguage.class,
      GlobalRulesDefinition.class,
      GlobalSonarWayProfile.class,
      GlobalSensor.class,
      GlobalExtension.class);
  }
}
