/*
ACR-1d7ecdaa744c46e0a27f78536107eba6
ACR-83074ca59368492b94134d76032c9ec9
ACR-d59d4d8a974645f2bf3c4b1dc59a323f
ACR-d3de3115e53d4263a8b340c271277e4a
ACR-a9943f2ab6b041f6abf6eef9b5abe8d2
ACR-01cf51bd0b5a46c794be9ed25c7ba58e
ACR-69e102a3817b4ce9bfdb13ef4e1b0194
ACR-4707e3faf8bd4e63bd24b2ab499f6529
ACR-4863894e75de46bd8dd1ea59fae8b255
ACR-410109bdd2a04cf3a19b48b464d5e24c
ACR-c349daed830a443d9a1ff90bef1049d9
ACR-e7a54cfbfdfc4d26affe9e8549a0efe2
ACR-f52a841c6cee4da3815ee23caff02521
ACR-22cc2f136e4d4648be6437516989c987
ACR-1c00fc0eeb564c2fa77e38521c68c391
ACR-acda14e90bf648cb9045e43be9b9f301
ACR-764fcd98d48945efb5b1e6e7c900ed4b
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.TextPointer;

public class DefaultTextPointer implements TextPointer {

  private final int line;
  private final int lineOffset;

  public DefaultTextPointer(int line, int lineOffset) {
    this.line = line;
    this.lineOffset = lineOffset;
  }

  @Override
  public int line() {
    return line;
  }

  @Override
  public int lineOffset() {
    return lineOffset;
  }

  @Override
  public String toString() {
    return "[line=" + line + ", lineOffset=" + lineOffset + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var other = (DefaultTextPointer) obj;
    return other.line == this.line && other.lineOffset == this.lineOffset;
  }

  @Override
  public int hashCode() {
    return 37 * this.line + lineOffset;
  }

  @Override
  public int compareTo(TextPointer o) {
    if (this.line == o.line()) {
      return Integer.compare(this.lineOffset, o.lineOffset());
    }
    return Integer.compare(this.line, o.line());
  }

}
