/*
ACR-18b73852db474920a69202eecb8ac4d8
ACR-c5455e58f862466c893606b159672422
ACR-a9be380564f9402fb8d5d67d0d51e464
ACR-0e47a1072e5c4eb587efbb80fa113821
ACR-fc24bb277e894a0dbb95b14ca434ab14
ACR-effcaf3a436645e38de4d36e3b973c16
ACR-033c461b36104b1dbd0b547b6a663b52
ACR-517fd5060ea04ec4b6d2a973b812f91e
ACR-45f15e1cc1194fb1afe95f131100d7cc
ACR-227ddbc8648344aeac5e177f36106e90
ACR-220951ff6bea4e6d857d946a2e4cec41
ACR-f3f2410c006041c384b1f183d3a74a30
ACR-82c2bcdd1f1249a192cecd1cbb635801
ACR-9eb5bdddf1fd4203a7d72af1bca10fc8
ACR-b860ba96891d4e2b8a64d4d024dfe5cb
ACR-3b8865feebb14fbd89d1e5fb7622177e
ACR-a7bafe94cca34977a58973a0045ea2b1
 */
package org.sonarsource.sonarlint.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*ACR-c98bd37f43774e3da313bc5d4c2666ff
ACR-1799a1b7eb074b00872629d1456c8946
ACR-f48f68f49e2e43d7a00fe6e5919064f4
ACR-b5dc892efdc24396bf19ce0034957cbf
ACR-a59a95b01b444b019c6ef15d02c2251d
ACR-54fe8b6fdde4404da77057668872c7e0
ACR-8d687bff32c147b2a8537d10e39b91bd
ACR-81d004aa38974d1b8ebaac2d7712226b
ACR-e89bce429d8c4abe8e63b998b54e4905
ACR-4645c0ae797b475eb0a91fc2923ef011
ACR-0c26bf2c875c44db91168d3d972da5c9
 */
public class TextSearchIndex<T> {

  /*ACR-39c19ed96f584f0fb2120fa69bdf4db9
ACR-58adb0d155604f9ba3010eb6139d856c
   */
  private static final String DEFAULT_SPLIT_PATTERN = "[^a-zA-Z0-9]+";
  private final Pattern splitPattern = Pattern.compile(DEFAULT_SPLIT_PATTERN);
  private final TreeMap<String, List<DictEntry>> termToObj;
  private final Map<T, Integer> objToWordFrequency;

  public TextSearchIndex() {
    termToObj = new TreeMap<>();
    objToWordFrequency = new HashMap<>();
  }

  public int size() {
    return objToWordFrequency.size();
  }

  public boolean isEmpty() {
    return objToWordFrequency.isEmpty();
  }

  public void index(T obj, String text) {
    if (objToWordFrequency.containsKey(obj)) {
      throw new IllegalArgumentException("Already indexed");
    }
    var terms = tokenize(text);
    objToWordFrequency.put(obj, terms.size());

    var i = 0;
    for (String s : terms) {
      addToDictionary(s, i, obj);
      i++;
    }
  }

  /*ACR-15e20544244641bb95eaf1461ad17d8f
ACR-6ed9deafe88b4ba28a5185d864291aca
ACR-7eb13a9964784195a7166d1fe26f7016
ACR-1a9c944e0918431cac6843911a8dacec
ACR-e5f3039b3b824ba6b5ce99118a7324e5
   */
  public Map<T, Double> search(String query) {
    var terms = tokenize(query);

    if (terms.isEmpty()) {
      return Collections.emptyMap();
    }

    List<SearchResult> matched;

    //ACR-2d31f34603e2489ea06e5ebeece10989
    var it = terms.iterator();
    matched = searchTerm(it.next());

    while (it.hasNext()) {
      var termMatches = searchTerm(it.next());
      matched = matchPositional(matched, termMatches, 1);

      if (matched.isEmpty()) {
        break;
      }
    }

    //ACR-963887b1c340424ea3e41ab1956dbeb7
    return prepareResult(matched);
  }

  private List<SearchResult> matchPositional(List<SearchResult> previousMatches, List<SearchResult> termMatches, int maxDistance) {
    List<SearchResult> matches = new LinkedList<>();

    for (SearchResult e1 : previousMatches) {
      for (SearchResult e2 : termMatches) {
        if (!e1.obj.equals(e2.obj)) {
          continue;
        }

        var dist = e2.lastIdx - e1.lastIdx;
        if (dist > 0 && dist <= maxDistance) {
          e2.score += e1.score;
          matches.add(e2);
        }
      }
    }
    return matches;
  }

  private Map<T, Double> prepareResult(List<SearchResult> entries) {
    Map<T, Double> objToScore = new HashMap<>();

    for (SearchResult e : entries) {
      var score = e.score / objToWordFrequency.get(e.obj);
      var previousScore = objToScore.get(e.obj);

      if (previousScore == null || previousScore < score) {
        objToScore.put(e.obj, score);
      }
    }

    return objToScore.entrySet().stream()
      .sorted(Map.Entry.<T, Double>comparingByValue().reversed())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  /*ACR-3a415bb68916454aa9854b8428f3cb25
ACR-9f6506113c464d24a08487b1b5e3fe07
   */
  private List<SearchResult> searchTerm(String termPrefix) {
    List<SearchResult> entries = new LinkedList<>();

    var tailMap = termToObj.tailMap(termPrefix);
    for (Entry<String, List<DictEntry>> e : tailMap.entrySet()) {
      if (!e.getKey().startsWith(termPrefix)) {
        break;
      }
      var score = ((double) termPrefix.length()) / e.getKey().length();
      e.getValue().stream()
        .map(v -> new SearchResult(score, v.obj, v.tokenIndex))
        .forEach(entries::add);
    }

    return entries;
  }

  private void addToDictionary(String token, int tokenIndex, T obj) {
    var entries = termToObj.computeIfAbsent(token, t -> new LinkedList<>());
    entries.add(new DictEntry(obj, tokenIndex));
  }

  private List<String> tokenize(String text) {
    var split = splitPattern.split(text);
    List<String> terms = new ArrayList<>(split.length);

    for (String s : split) {
      if (!s.isEmpty()) {
        terms.add(s.toLowerCase(Locale.ENGLISH));
      }
    }

    return terms;
  }

  public List<T> getAll() {
    return List.copyOf(objToWordFrequency.keySet());
  }

  private class SearchResult {
    private double score;
    private final T obj;
    private final int lastIdx;

    public SearchResult(double score, T obj, int lastIdx) {
      this.score = score;
      this.obj = obj;
      this.lastIdx = lastIdx;
    }
  }

  private class DictEntry {
    T obj;
    int tokenIndex;

    public DictEntry(T obj, int tokenIndex) {
      this.obj = obj;
      this.tokenIndex = tokenIndex;
    }
  }
}
