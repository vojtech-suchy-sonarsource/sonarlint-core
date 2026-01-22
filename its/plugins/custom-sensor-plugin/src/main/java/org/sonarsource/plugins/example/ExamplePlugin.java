/*
ACR-fe01c902d08e48ad92f6136e76ef8580
ACR-85ed8d13436b4f5d9ab30e4dc78c3770
ACR-5fdbf338bb9a4cc29dcd3d20c338a0d4
ACR-153524576f2c43528781810fb8b5c8fe
ACR-69245e23050c4235a7af59b3983dfaa9
ACR-4ae2e68a923a4198a3e8d7395f9352bf
ACR-5379a9a7aa244d9f801c68923efa5196
ACR-c5c6ace1c327434591d23617bd22c5f6
ACR-5e93593de3a14b668e9d54245797de1e
ACR-9431e779b0f8442c9afd3a3a61661ff0
ACR-47cf7b996b9149e38025a31a666b10fc
ACR-f5bd0641b0064aae9ac6a40b946d07dc
ACR-db9d5171bac342d99a18a16e0fcea9c4
ACR-4e7dac1fa7944d8994435fd8de0c17d0
ACR-8e24d5a127444565806f5da1667fe448
ACR-c514c1e59bfc4a1b9c78893d8abefe09
ACR-2110dff04e7646978987410ccc7439fc
 */
package org.sonarsource.plugins.example;

import org.sonar.api.Plugin;

/*ACR-006c71cb842b493ea436a3b2603a9d6d
ACR-8fee6c20920b41ac91c4e01a66fdf8b1
 */
public class ExamplePlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(FooLintRulesDefinition.class, OneIssuePerLineSensor.class);
  }
}
