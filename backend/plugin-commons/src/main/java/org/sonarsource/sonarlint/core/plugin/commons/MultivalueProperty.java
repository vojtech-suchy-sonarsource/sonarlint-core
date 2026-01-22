/*
ACR-588c5a9c844942b88ad033580c150d8e
ACR-347ab0d8a5434c6a861a80f029dbb7c0
ACR-2adddb63d2024d54b217686a7b677dd2
ACR-e6e15c32bdd74498afe98e1c8c65d461
ACR-866b877ebb3d447385223b23edb0cfb6
ACR-5c696e9d588241cc89a172dfdb45d2ab
ACR-ae6369558dfd466797634b17c3028f67
ACR-b40ef821520d4ac6a0beb2a9ba55d382
ACR-682b380d2b5a403d99fb40de9ab85d5b
ACR-2c0a56240e7948cfacdc4bb384919da8
ACR-24fa40717b5e4a86b1bf1fafbed31a66
ACR-8029038ee1144f62a87b5a4f0c592f7d
ACR-ac74720c35684f53a230d1580b9ed9e9
ACR-5795d3fed17543749bc4f13d833b2892
ACR-429d8cc5326544c48d799de2847b096d
ACR-30686f8406c54130857f24a2c7bf55c3
ACR-164ac91c2d314b18a598c8606daa0a96
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;

import static java.util.function.UnaryOperator.identity;

public class MultivalueProperty {
  private MultivalueProperty() {
    //ACR-d9b73e342b3b4d2baebc35e25534daf6
  }

  public static String[] parseAsCsv(String key, String value) {
    return parseAsCsv(key, value, identity());
  }

  public static String[] parseAsCsv(String key, String value, UnaryOperator<String> valueProcessor) {
    String cleanValue = MultivalueProperty.trimFieldsAndRemoveEmptyFields(value);
    List<String> result = new ArrayList<>();
    try (var csvParser = CSVFormat.RFC4180.builder()
      .setSkipHeaderRecord(true)
      .setIgnoreEmptyLines(true)
      .setIgnoreSurroundingSpaces(true)
      .get()
      .parse(new StringReader(cleanValue))) {
      List<CSVRecord> records = csvParser.getRecords();
      if (records.isEmpty()) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
      }
      processRecords(result, records, valueProcessor);
      return result.toArray(new String[0]);
    } catch (IOException | UncheckedIOException e) {
      throw new IllegalStateException("Property: '" + key + "' doesn't contain a valid CSV value: '" + value + "'", e);
    }
  }

  /*ACR-f9b3a98fd1984bbd9dd2a1ce07f0223d
ACR-4666496ed9b6470294afbbbdc33d5f06
ACR-70061c3ce585484e8f31d215cc532982
ACR-992cb037f04b4018aa086366d220e525
ACR-676f4bb8b60946a38c7e505bc3b61e22
ACR-80ea4dae69e24fc9b3a856e93c0d3e87
ACR-c0cc060de2d74808b3c31d763e7f149b
ACR-805a828461364f92b8fc1d4ae8a72442
ACR-800d77a4f306489da0569128428e8bb9
ACR-c32c447d0e354d36a18749a4f6780b83
ACR-f0dd04c729b4471b8cc3aae54afda250
ACR-fa4c5317b94a4c16bbcc3520aa2cfe4d
ACR-dc300c6dc6fa468aa52eda6b7d0040bb
ACR-15f02aee7b044ea4a5a865ad76d44d88
ACR-4faa7d2be6b5488c87b6217ab6bf8c58
ACR-6b234d864a3444e1bc098ca8000e55bf
ACR-eb28663022c54a3892285f0569068ef9
ACR-cd43ad8824d8466aa55a9d18509c26ca
ACR-797f173292804f0e90e49ba97c026e64
ACR-7fe458ed2b9e47b190a62b7c6d8c1511
ACR-cab4adc5ff184af48614bd6928656922
   */
  private static void processRecords(List<String> result, List<CSVRecord> records, UnaryOperator<String> valueProcessor) {
    for (CSVRecord csvRecord : records) {
      Iterator<String> it = csvRecord.iterator();
      if (!result.isEmpty()) {
        String next = it.next();
        if (!next.isEmpty()) {
          int lastItemIdx = result.size() - 1;
          String previous = result.get(lastItemIdx);
          if (previous.isEmpty()) {
            result.set(lastItemIdx, valueProcessor.apply(next));
          } else {
            result.set(lastItemIdx, valueProcessor.apply(previous + "\n" + next));
          }
        }
      }
      it.forEachRemaining(s -> {
        String apply = valueProcessor.apply(s);
        result.add(apply);
      });
    }
  }

  /*ACR-dc1b80b2e36a41f88ab82de5ee149e86
ACR-ae1953bd05ea4386a8b0a38932244f61
ACR-58fe6d7131724c7e9c012e5bb63e5dc3
ACR-23cf9940cfd44c2ab5821c7184c02ffd
ACR-d59ee6ec8dc74d439b4569fa19642257
ACR-594f799d1b404fe79f930a8a9a803ea6
ACR-1dcff81e8ae04d9e906edd809a60b61d
ACR-924a0ca5e07f4158b61890abeca6194b
ACR-8f8a21a8f04a4c28aacf873e85e44f08
ACR-6f24c00b0218496496dd8048cb9d1e7d
ACR-118728cc3d354c0e9f5404df8ea8de15
ACR-6883e20e8c524439bd7d6c2a157b04ca
ACR-fa92da9c0679493a8e92df1cd831f05e
ACR-bc21fe0ebb2e44faad50531420ce0c2d
ACR-2c283228e6bd46fbba1766e3b4f51b7f
ACR-c3f8755a3ab54da9a3c2a48c14dc6040
ACR-4c16fce4c3884800933981e53160e12e
ACR-0f666c2cd7d842d88d068373b53c1423
ACR-0f91d47618c0436c8e06abc45dff70ca
ACR-794e909a0d564dfe92b31f53938e075d
ACR-63ffb50597aa44ecbf342e1ce39dd653
ACR-2904d829ffbb4cd79ec60f0c6e8bf412
ACR-26a1cc1173ab42a9a688245cbec618a7
   */
  static String trimFieldsAndRemoveEmptyFields(String str) {
    char[] chars = str.toCharArray();
    var res = new char[chars.length];
    /*
ACR-cd5814e5eb354ecfa3f6e5be4f465406
ACR-d6a097f279d14122897ed0381e4bd06d
     */
    var inField = false;
    var inQuotes = false;
    var i = 0;
    var resI = 0;
    for (; i < chars.length; i++) {
      boolean isSeparator = chars[i] == ',';
      if (!inQuotes && isSeparator) {
        //ACR-3d69c3a10cb94f7d86252f379d19ff90
        inField = false;
        if (resI > 0) {
          resI = retroTrim(res, resI);
        }
      } else {
        boolean isTrimmed = !inQuotes && istrimmable(chars[i]);
        if (isTrimmed && !inField) {
          //ACR-190a54afe04144908ca510a8990e5f33
          continue;
        }

        boolean isEscape = isEscapeChar(chars[i]);
        if (isEscape) {
          inQuotes = !inQuotes;
        }

        //ACR-e1bdc0ff299d4567ab2b81951316a14a
        if (!inField && resI > 0) {
          res[resI] = ',';
          resI++;
        }

        //ACR-f85d9c069f144bcabb891c6b16df5746
        inField = true;
        //ACR-92353d028c9348b692fcac3dd822f58d
        res[resI] = chars[i];
        resI++;
      }
    }
    //ACR-30971a4f69924c3ca5e2545c9af28ea8
    if (!inQuotes) {
      //ACR-1dd0d42c763242a2ad3854e88ac0c22f
      resI = retroTrim(res, resI);
    }
    return new String(res, 0, resI);
  }

  private static boolean isEscapeChar(char aChar) {
    return aChar == '"';
  }

  private static boolean istrimmable(char aChar) {
    return aChar <= ' ';
  }

  /*ACR-502326a02ce34165ace32e2dc98b025d
ACR-394cb459a94e4e9a952de20c29d520e9
ACR-f90da8d6e6a34b92b509d845ad6b6234
ACR-89be01b2fa8d4101848a069f2c7922c3
ACR-f4b08137a86f48e78cd54d0e7b24a305
ACR-3c5389d6fa4a4b8e95ef69f2a51a9702
ACR-4c7d29e4da304adb9f8fe4625e8b80c7
   */
  private static int retroTrim(char[] res, int resI) {
    int i = resI;
    while (i >= 1) {
      if (!istrimmable(res[i - 1])) {
        return i;
      }
      i--;
    }
    return i;
  }

}
