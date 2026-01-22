/*
ACR-9e51195ad21244fd94000257952276b4
ACR-4f25683ca237422d9b47dde0d0bce6f1
ACR-82098d8847d340ff835934650fccc6c3
ACR-98075aefa6764348bbe271cbf613fb3a
ACR-8c143fe9112149fa9aa0f29103cbf36d
ACR-da065e7678454bf9aad4a59a99c714db
ACR-8337dacd70cc42ec943ddffc1797dc89
ACR-59388c74b80a47d6aabc38ce4f6f240e
ACR-c449353a960b4b5680613018e37f3c8e
ACR-0b2351be0c534b05a51a1f06d57f9cbd
ACR-b02d40d0dea54a5398b98d49cd52fa6e
ACR-eec0af3feb1641f88ba1046d0fbc1359
ACR-5065be3e698448ef8b334088efae56bb
ACR-50537e32b0b94629b428fe40dac31083
ACR-846fda86d7e0430e9ca6c1d302714215
ACR-171090b41bd346db8c980ce5102cbdc5
ACR-590df860655541698df60b773594f06f
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-32065cae92064719b75805e0d9e53335
ACR-3415b8b08ed442a299a9ea8625c48eae
ACR-28f86be01bfe4d08a976dce67e1433e3
 */
@SonarLintSide
public class FileMetadata {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final char LINE_FEED = '\n';
  private static final char CARRIAGE_RETURN = '\r';

  public abstract static class CharHandler {

    protected void handleAll(char c) {
    }

    protected void handleIgnoreEoL(char c) {
    }

    protected void newLine() {
    }

    protected void eof() {
    }
  }

  private static class LineCounter extends CharHandler {
    private int lines = 1;
    boolean alreadyLoggedInvalidCharacter = false;
    private final URI fileUri;
    private final Charset encoding;

    LineCounter(URI fileUri, Charset encoding) {
      this.fileUri = fileUri;
      this.encoding = encoding;
    }

    @Override
    protected void handleAll(char c) {
      if (!alreadyLoggedInvalidCharacter && c == '\ufffd') {
        LOG.warn("Invalid character encountered in file '{}' at line {} for encoding {}. Please fix file content or configure the encoding.",
          fileUri,
          lines, encoding);
        alreadyLoggedInvalidCharacter = true;
      }
    }

    @Override
    protected void newLine() {
      lines++;
    }

    public int lines() {
      return lines;
    }

  }

  private static class LineOffsetCounter extends CharHandler {
    private int currentOriginalOffset = 0;
    private final List<Integer> originalLineOffsets = new ArrayList<>();
    private int lastValidOffset = 0;

    public LineOffsetCounter() {
      originalLineOffsets.add(0);
    }

    @Override
    protected void handleAll(char c) {
      currentOriginalOffset++;
    }

    @Override
    protected void newLine() {
      originalLineOffsets.add(currentOriginalOffset);
    }

    @Override
    protected void eof() {
      lastValidOffset = currentOriginalOffset;
    }

    public List<Integer> getOriginalLineOffsets() {
      return originalLineOffsets;
    }

    public int getLastValidOffset() {
      return lastValidOffset;
    }

  }

  /*ACR-beda8596ffdb43b1ace29d48a53a69c8
ACR-dcaae3571523433a9cc428660b07ee0e
ACR-0042085393474b9681d29734304d532e
   */
  public Metadata readMetadata(InputStream stream, Charset encoding, URI fileUri, @Nullable CharHandler otherHandler) {
    var lineCounter = new LineCounter(fileUri, encoding);
    var lineOffsetCounter = new LineOffsetCounter();
    try (Reader reader = new BufferedReader(new InputStreamReader(stream, encoding))) {
      CharHandler[] handlers;
      if (otherHandler != null) {
        handlers = new CharHandler[] {lineCounter, lineOffsetCounter, otherHandler};
      } else {
        handlers = new CharHandler[] {lineCounter, lineOffsetCounter};
      }
      read(reader, handlers);
    } catch (IOException e) {
      throw new IllegalStateException(String.format("Fail to read file '%s' with encoding '%s'", fileUri, encoding), e);
    }
    return new Metadata(lineCounter.lines(), lineOffsetCounter.getOriginalLineOffsets().stream().mapToInt(i -> i).toArray(), lineOffsetCounter.getLastValidOffset());
  }

  private static void read(Reader reader, CharHandler... handlers) throws IOException {
    char c;
    var i = reader.read();
    var afterCR = false;
    while (i != -1) {
      c = (char) i;
      if (afterCR) {
        for (CharHandler handler : handlers) {
          if (c == CARRIAGE_RETURN) {
            handler.newLine();
            handler.handleAll(c);
          } else if (c == LINE_FEED) {
            handler.handleAll(c);
            handler.newLine();
          } else {
            handler.newLine();
            handler.handleIgnoreEoL(c);
            handler.handleAll(c);
          }
        }
        afterCR = c == CARRIAGE_RETURN;
      } else if (c == LINE_FEED) {
        for (CharHandler handler : handlers) {
          handler.handleAll(c);
          handler.newLine();
        }
      } else if (c == CARRIAGE_RETURN) {
        afterCR = true;
        for (CharHandler handler : handlers) {
          handler.handleAll(c);
        }
      } else {
        for (CharHandler handler : handlers) {
          handler.handleIgnoreEoL(c);
          handler.handleAll(c);
        }
      }
      i = reader.read();
    }
    for (CharHandler handler : handlers) {
      if (afterCR) {
        handler.newLine();
      }
      handler.eof();
    }
  }

  public static class Metadata {
    private final int lines;
    private final int[] originalLineOffsets;
    private final int lastValidOffset;

    public Metadata(int lines, int[] originalLineOffsets, int lastValidOffset) {
      this.lines = lines;
      this.originalLineOffsets = originalLineOffsets;
      this.lastValidOffset = lastValidOffset;
    }

    public int lines() {
      return lines;
    }

    public int[] originalLineOffsets() {
      return originalLineOffsets;
    }

    public int lastValidOffset() {
      return lastValidOffset;
    }
  }
}
