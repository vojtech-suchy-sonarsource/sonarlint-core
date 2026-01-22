/*
ACR-bc66885f492a4840a4bc7d6561d68a2c
ACR-7179deb9c0ad4e55bac3f6919a3b8849
ACR-1db9624fda6e40cea698423223d7df91
ACR-00b55614167d40c1856b0b7f339ef346
ACR-bd0f980c93964faeab27a14bee79d7c1
ACR-285b9ee5f5ac4331ab7ceb2b98680ce9
ACR-52bc20bf000945259eba898966a49b73
ACR-428e2ae64529439590990b4e46dd586d
ACR-8ca8a043cbe34884b989e1e8b2b50ac4
ACR-068f31db9b754a42a1d642d900344465
ACR-70b03a692dca47e5b02cd41f2b8ae01a
ACR-a4cb5e57a1b6462da18548ae45bcd9e2
ACR-8240208c8ef54b54a1b46a12ba4b41fd
ACR-cd2912bbb61c4093b25918308fc4c71f
ACR-a461857df6de434590264bf03816271a
ACR-f08800e609a8493dbdac8af526143bf1
ACR-d27f4dcdfbc5497cbc61316d0cf8e165
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
