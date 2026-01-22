/*
ACR-d7330df0e96a4e439f3b30e3a4b05f96
ACR-c63be466cb9c4f5eb74fcd07ade61e90
ACR-5407e486d90546df8dc78e93e648af03
ACR-eccc5128a1dc45c5b9de1f2b480a7663
ACR-e7095e66315744ce89ddff9bcb946de9
ACR-a89ea77ba7564f7e8fca880e5cc9c8a1
ACR-e0ddca831b204e1fbfd968868e743d77
ACR-f4593b8a93b94801bec9748ababe5181
ACR-a88db8b95db446269fe10435f715cd88
ACR-4e566e392af941df8011bc681f4b651c
ACR-b3d3597002844c2981b63c6b6d41c1e0
ACR-40d5a83f8a3a4fd78a2d1225f99631bc
ACR-466091ca7b3245928734334baf23cd8a
ACR-4a939658ec6341ee93df5c4f8bae5d54
ACR-b8e7c6e322904b9186be4c2d091e753c
ACR-c565f0c20f714ecab28be46f2c5ef848
ACR-228576f8ceee4997b1a912900b3f11b6
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;

/*ACR-db19848e35244ba0b7c1d884cd956f17
ACR-3fdb0007bca64db983b3c9c6712c9f38
 */
class TypePredicate extends AbstractFilePredicate {

  private final InputFile.Type type;

  TypePredicate(InputFile.Type type) {
    this.type = type;
  }

  @Override
  public boolean apply(InputFile f) {
    return type == f.type();
  }

}
