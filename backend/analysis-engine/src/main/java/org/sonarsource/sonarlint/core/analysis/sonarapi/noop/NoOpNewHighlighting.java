/*
ACR-96e3fabf3f934906a72fcc0e718ba969
ACR-e82877e1fbf244769db3d3fbae5e2666
ACR-b1f1644db6274ff8ac1face3b53d9e46
ACR-e49aa5a840b4478b979cd5d88c0b2a6d
ACR-ef2dbdf20afa47d9915d8c37652b1819
ACR-e91363aa8785403cb4dad088bacf3bf5
ACR-51499e3f2b3c447a90bd45b0346434a5
ACR-19bd5cc5aa5c4d98ae82f1284b1628b7
ACR-0dc321715b0c4d689e3551b0d1e109e4
ACR-55945a198f6f4f8e88df9e693c8e9f4e
ACR-a8ce1c14be264499b36d9c77bf8a75b5
ACR-0e7ab8b030b34a75a63d2413f54f8869
ACR-c02182b713e94001bdb966fe68330441
ACR-a4fbe39e57df4c6584d4d07bb9ea645b
ACR-038b3fae8bde4e57b4d7b548bfb39fad
ACR-b7a9a0de0675450d9ceeb8aeb63c625b
ACR-00c007de6eaa43b8842239946357c8ef
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

public class NoOpNewHighlighting implements NewHighlighting {
  @Override
  public void save() {
    //ACR-33ed246d113a404db0983351f8ca028a
  }

  @Override
  public NoOpNewHighlighting onFile(InputFile inputFile) {
    //ACR-5d530c0d368e4dc9975005bdc5faa1ca
    return this;
  }

  @Override
  public NoOpNewHighlighting highlight(int startLine, int startLineOffset, int endLine, int endLineOffset, TypeOfText typeOfText) {
    //ACR-b6ddfb32be694f36be76d4fbd8faa1fb
    return this;
  }

  @Override
  public NoOpNewHighlighting highlight(TextRange range, TypeOfText typeOfText) {
    //ACR-035fd3718c8044c1aa91228475cfbf84
    return this;
  }
}
