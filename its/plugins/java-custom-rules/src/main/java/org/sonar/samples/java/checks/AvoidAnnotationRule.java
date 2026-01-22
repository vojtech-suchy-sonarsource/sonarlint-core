/*
ACR-3cd4b68374d64e88be5e36f49b68c6e4
ACR-35a164d75fc1450a8fb22a1ebaeb8010
ACR-3d529b261c9543f9b789a435dbc79ade
ACR-cc8a62f7a6794fdba86c8b793e4fe61f
ACR-14397c6a1cb744e094ba08f3eda1dc36
ACR-136158c157d443a8b72c71a2da1ffcf1
ACR-1ea8f5c8c3c34696bca9569cd9e224fd
ACR-fa0c2dc1c8494db69e8a72dd2f184275
ACR-0df8cf66cc194b69a44e979e5adfab99
ACR-853d85093c2c403681069e4146df81f5
ACR-303630f008dc4582b98aae06d89e8d98
ACR-d0827188124f4691a317b9a2c2fd37e4
ACR-bfc055e43e1a4dc3ae230ac0acf27fdf
ACR-206d5a80afb54c8190ea5a74b90b975e
ACR-3212edb4de264fb49dfb58a3b44c33c3
ACR-3bdffdd8196840828f7b01dea5b07290
ACR-33ea0e06768a4ed795618e843d603005
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

  /*ACR-949ec0d6db7a4923904a658e86b06e1c
ACR-9d2545492f064e8798d1e468feab28db
ACR-5acc78e0aeea41ca8b427d9a1a5ba646
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

    //ACR-ae487421d8c249a4a6714119ba1937ae
    //ACR-ce8d094fa6814f03b757491eb77f4d76
    super.visitMethod(tree);
  }
}
