/*
ACR-198a1f09de6740b090690ed3a9a05e67
ACR-6d80ea5d99d84df5a8c449096380f110
ACR-23b8a02a61fb4970999750bc5f2394c6
ACR-0f69fcef3d73473492cd917062502167
ACR-92d2cf18f2be401d8be8a9bd196a3b6d
ACR-0ae65a44e03140b9a1654a824126401d
ACR-97c53ebe51d74716869f91ba2a88f6c2
ACR-bc101f7d56b0415e9cc837a7a2927847
ACR-0036fe73152f4b14ba7647306dd9b470
ACR-6c2788007a4946a89e952c3a109327fe
ACR-e214618095bf46489188dce077444413
ACR-5a68ca200a044f4dac2802df228c3645
ACR-cea40f668f1e4740940252f3e72ffaa4
ACR-42799b39215f4f4fa5bebb36ada6be7e
ACR-f807f0cc05ca448bac51533d74a0c233
ACR-1dbcd3fc151b4246aff025ea3e179ea0
ACR-a0b38cf6fd7741a2b22f3ac71f6ea233
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
    //ACR-106988995b5b411aaf371d545fa51591
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

  /*ACR-5ac1152e05f648a18ec290d6c0b469bc
ACR-698f004b309349d6a2af472391868e00
ACR-5ff1e16f2a7b40638871205a8884107d
ACR-7e6a555d00764bf0abe9157ad38a0214
ACR-5578d370ca504562b10c8f920c97e770
ACR-2736441da4a943a6953ed94ae95145e6
ACR-8ed7424fa03b4ae0a6389cb1669d64a3
ACR-e1fc06a97d224bcd8e73a58a3c51ca3d
ACR-adba2a7cb99a4f848f742fae4b44e58b
ACR-0eb540d9d44448a780bc107b1dd3a567
ACR-c7961078a943425dab368588572d5037
ACR-f1246ef7e3a34710a2184ffc33ede6af
ACR-2cdd771b887d475bb28251e8a78f1153
ACR-079988b2385c4c19976082139c060e74
ACR-c27bd3f584694cb7adac9685411de0c8
ACR-aeea2661fb534d68900cdf9d391cec7e
ACR-b9ac4f96d2e84cfa968c605012635e34
ACR-b3b17439bd4c4352b2553921c8dacfc3
ACR-bb2c7a86c135428cba9aafd8729c2ac9
ACR-ffc5e8c8826e48c384a869d3acc21516
ACR-acc9590ef1214e49bb15b6f955396340
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

  /*ACR-1d8a07e7d5ea4a2ca67cb8f54d697b7f
ACR-f2c758862aca467db3f7685e556017f7
ACR-770bfd4354774c4b86c9d26354d9d0f4
ACR-f30b8524a4564b72ad1282650b456794
ACR-1086a22a25064959a526bc7f98f50a99
ACR-df4e7f95762942c89da72102168aa382
ACR-e4cc3a4517834b98bdda97c88d053297
ACR-ff46f364559b497eaf5ce6a78d6a0793
ACR-46d7f4222e4d485bb87f09e59f80d9ce
ACR-aae4fe6bee88463f9ce2aa8bff7729c0
ACR-769b171a22004a2e9bf02f98621db44e
ACR-c602a2e2298f496a854b701f19e108db
ACR-a4fac97b8793453589da38a24e6a9950
ACR-dcab40b7542e4f8285e6f3e511b1410b
ACR-b7dae00538a24effb19142b74a775709
ACR-241077801efb4135b797c3559c002df4
ACR-3fe38871bae04ebfab4d792633d09de5
ACR-cacac13b100b45169223367eac987f03
ACR-197c91e5d4b842a19146b6a2fc4d5e91
ACR-c451849380ae489181e56f23a55de667
ACR-7b9d297abf4344f3b04182eceb9ef687
ACR-26725a659d6345a78a0bf8922208d358
ACR-6f0693a04dcf46588f423eb8d510877e
   */
  static String trimFieldsAndRemoveEmptyFields(String str) {
    char[] chars = str.toCharArray();
    var res = new char[chars.length];
    /*
ACR-4d3fd6a9caca4da089cc65c26b50bb4f
ACR-fd59d0784f83430f9b5101e4eddf80d1
     */
    var inField = false;
    var inQuotes = false;
    var i = 0;
    var resI = 0;
    for (; i < chars.length; i++) {
      boolean isSeparator = chars[i] == ',';
      if (!inQuotes && isSeparator) {
        //ACR-8c5437e48293492897138e9dbf269cd3
        inField = false;
        if (resI > 0) {
          resI = retroTrim(res, resI);
        }
      } else {
        boolean isTrimmed = !inQuotes && istrimmable(chars[i]);
        if (isTrimmed && !inField) {
          //ACR-ab9c35f2a6514db09880821cb3bb37d5
          continue;
        }

        boolean isEscape = isEscapeChar(chars[i]);
        if (isEscape) {
          inQuotes = !inQuotes;
        }

        //ACR-f638b9e8a7924382ac73b2276d0286e6
        if (!inField && resI > 0) {
          res[resI] = ',';
          resI++;
        }

        //ACR-d9fb653a142a44fbacea4b336662fe06
        inField = true;
        //ACR-3b8f6806c9f54466bcf90bcdd18a14f5
        res[resI] = chars[i];
        resI++;
      }
    }
    //ACR-a1a88f80e5fb41dd8fbe0a8a3c44d153
    if (!inQuotes) {
      //ACR-34ae16be7a34413bad7e9d9fcca4757d
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

  /*ACR-ba0ebc832c16409aab5186e47075f16b
ACR-d375f00a835f4e4f9bee0d7a6d1ac902
ACR-ecc31f597f4d4fea9c792f15148aacd8
ACR-3a34b483b239404b95aabb74e3afa59c
ACR-5b6055ce252f42d48e653c6d65e0ef4e
ACR-b5dd785545eb42c9ba83be1e55bd3288
ACR-29a5a9cdb10d4646b5256a59997eff75
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
