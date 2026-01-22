/*
ACR-19f08b6d71fd4d2d8b395205fcef5ece
ACR-eb7f7138df5b451681444d1e069f3393
ACR-a74d8202daef4fb4a86c4ffd9326315d
ACR-27e00c526dc04d3fa576e82c8b73d20c
ACR-a02f6262499c400e8aa518fef393450c
ACR-a57e1358c76e406f99dbb3c0a4ca742c
ACR-42fdd0ac7d7544feba9f7cd7e5cb3b86
ACR-9b15468885a1442e99191d72967a3b7e
ACR-ec21c099193946d9a3cdcd574c25f24b
ACR-07a1038450a54f00b5a35d5af55dc28d
ACR-e26d10ec2ddc4a62b9b30f284b17ea79
ACR-f57a9731e0a44eab8e62f1faa80b68d7
ACR-59f7301c862340adbfb302f88a8a7765
ACR-168758af2d9949448cac09dc3e461816
ACR-233f907b12ee409ba23d95436d753591
ACR-1a30934f60af451db4c1575ac07c4e5e
ACR-f5775001346b43ebab9507e9c621633c
 */
package org.sonar.samples.java.checks;

import java.util.List;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TypeTree;

@Rule(key = "AvoidAnnotation")
public class AvoidAnnotationRule extends BaseTreeVisitor implements JavaFileScanner {

  private static final String DEFAULT_VALUE = "SuppressWarnings";

  private JavaFileScannerContext context;

  /*ACR-793e0d78b306453f8507f9b4c125cd13
ACR-d0c15125264f4b1292989c73383c62fd
ACR-6762a7a405814f6797f6e20665c30b74
   */
  @RuleProperty(
    defaultValue = DEFAULT_VALUE,
    description = "Name of the annotation to avoid, without the prefix @, for instance 'Override'")
  protected String name;

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;
    scan(context.getTree());
  }

  @Override
  public void visitMethod(MethodTree tree) {
    List<AnnotationTree> annotations = tree.modifiers().annotations();
    for (AnnotationTree annotationTree : annotations) {
      TypeTree annotationType = annotationTree.annotationType();
      if (annotationType.is(Tree.Kind.IDENTIFIER)) {
        IdentifierTree identifier = (IdentifierTree) annotationType;
        if (identifier.name().equals(name)) {
          context.reportIssue(this, identifier, String.format("Avoid using annotation @%s", name));
        }
      }
    }

    //ACR-3d332b9c7e254b049abad7ab9f8e5850
    //ACR-e963a688472148598457c964bca19c46
    super.visitMethod(tree);
  }
}
