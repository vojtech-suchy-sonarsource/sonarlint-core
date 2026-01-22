/*
ACR-ffb20e4a50804b90a6c10a505e838527
ACR-7ebc2b839e624bac9c5674c2d88c7434
ACR-0f9028748e9747b18aa9e5ac19fdf5be
ACR-810ec0bc604b4662a05ad38176e481c4
ACR-ad78a848fbe34a0cb9102838c7763114
ACR-1418a11f794f4de2881a43ce587e8e0d
ACR-359e47a62681462a9596708fbd41074d
ACR-d9221ef3997a4d32b184159630db3654
ACR-5decbef6dd1d448a8b8bdf97cbf06410
ACR-db3f8389c23a4ae8a4ba0680d8cf02f1
ACR-cace5575bab34ce8b64b590f24c89f00
ACR-56383b81855f48dcaddbde66631dc336
ACR-5c1fe94be94d475d8e7941cf4bcd5414
ACR-3add2f4210764e128aa208a8089fd055
ACR-ffa815df88f946879ec9349a7e5accaa
ACR-175f5a547202497f851c10e068f94364
ACR-f72ed3cdf40a4a8facc04902f23403d4
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

/*ACR-6748dd01d2724e14abfdbcb169e2527b
ACR-01ed118520384a8db396d39f3e077d1e
ACR-19ada440408a4cadb67dc7dfe3d9758f
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

  /*ACR-15e8237771d34d52828d489756e789a1
ACR-457cc34549ec4351aafbdecb733f959a
ACR-d7d38804b9ac4a5aab7e615cf6d3d496
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
