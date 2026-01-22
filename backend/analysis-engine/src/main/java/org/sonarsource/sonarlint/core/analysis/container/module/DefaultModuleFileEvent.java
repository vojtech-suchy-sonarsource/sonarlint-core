/*
ACR-94741d74c08a4b3ea8bffe2230ee4cc8
ACR-fb25357686964cbd8c726258e79c0f32
ACR-dfbf0b13e93245ea88d4dbffe3ecf94e
ACR-66fdac203ec7422aaf0e09345072a4a5
ACR-93178223fe5947c799acd94cb75510d3
ACR-4aadf22832c14783984b3949c4498fd8
ACR-95c7cb4ee5614bedb4a8792cae3debad
ACR-8efc2d768e3249b48f54a77a281eea52
ACR-79d67ce135274ee49e7d152b9851de8b
ACR-122c69504e1f477abec7e83cdcf60dd5
ACR-c9edf74f72d8454d8dbd02e42e75eba5
ACR-19cb0a21baf244b5b5fca361e9a3e528
ACR-58baccdd6a914fdf804adea14fe608a0
ACR-710536ad289944278ed9c9656259f352
ACR-ed4e985b167647cea82845e647b04aad
ACR-f59750a5a97c46ce8b10cb6655e178f3
ACR-249e9b491c354c21b2581eeafd06589e
 */
package org.sonarsource.sonarlint.core.analysis.container.module;

import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileEvent;

public class DefaultModuleFileEvent implements ModuleFileEvent {

  private final InputFile target;
  private final ModuleFileEvent.Type type;

  private DefaultModuleFileEvent(InputFile target, Type type) {
    this.target = target;
    this.type = type;
  }

  public static DefaultModuleFileEvent of(InputFile target, Type type) {
    return new DefaultModuleFileEvent(target, type);
  }

  @Override
  public InputFile getTarget() {
    return target;
  }

  @Override
  public Type getType() {
    return type;
  }
}
