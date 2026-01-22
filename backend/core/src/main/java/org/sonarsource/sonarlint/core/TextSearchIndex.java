/*
ACR-dd08f69a613b4e42bc8ddbb176721e5d
ACR-595a30e565a844fbbdae8235c495894d
ACR-035033edc93749ac8989b9609b5019d7
ACR-f355dd506c554b6795b9a309f568d960
ACR-15f93d0dae6f4c2092715d9e47e550ab
ACR-913f3c7420e4409aa7dacb352792bb6d
ACR-c8adf0029a1741a2bc05e38e40315b70
ACR-f4b860534ac04cb0837875d02ef1405e
ACR-288ed689666f4889b9bf785a8afb78be
ACR-113a564f547a4ba693a154fba4060fe8
ACR-136c6313e5f448c587c61cca2b1e23d8
ACR-e513a78d99c146cf9f820f68405fc96f
ACR-8c93f1999abc4f01aa5e0605ca8a44a2
ACR-3324a97af7ae40e1b8f8c66599c43f2e
ACR-cadaa0f6ffb24c23aebaf0cecf34470c
ACR-df3f0cb428b348ac84da97394c67766e
ACR-8ae7da6d737742bfa6cbfe8c2f53ad5c
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

/*ACR-1e048d25c77e4fb0abf34d1c0eb5a175
ACR-f96b2dec1e8c48929131564eebdfe35f
ACR-c0e9aca298de480dbec5d2a9b054c041
ACR-903c29ebd0424c2893cf0eebce766bf3
ACR-525d79f67edc47b6aa3bebee93793e96
ACR-341427bfe6854987af2d97788857d83c
ACR-40c65899e9a1471f9569526be94bc256
ACR-ba4db5a8cc30456abff58794ef4e5e1e
ACR-7a0a498f50b94484b62dc10b164bf4b6
ACR-a6c02ea230aa49b8a405a074eab7317a
ACR-17b32af25b8b4a4bb74d68aea0431f4a
 */
public class TextSearchIndex<T> {

  /*ACR-9f8e0807908f4d3eae6c1b89abcc8277
ACR-b249fef5151141e7a73e859329d06093
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

  /*ACR-6175c79ecade431fb5151339fe766ab9
ACR-02e5821e2ef9404b8f065e1873d9bc4e
ACR-2a38399f7fcd4df1891d9af29894ff0b
ACR-6f6459ec130d49b6a3e52aa90bb0367f
ACR-8227fbf9c0fa43cdabcdfa7e2f464256
   */
  public Map<T, Double> search(String query) {
    var terms = tokenize(query);

    if (terms.isEmpty()) {
      return Collections.emptyMap();
    }

    List<SearchResult> matched;

    //ACR-4f17f55e1ac043a09c19f667fafea276
    var it = terms.iterator();
    matched = searchTerm(it.next());

    while (it.hasNext()) {
      var termMatches = searchTerm(it.next());
      matched = matchPositional(matched, termMatches, 1);

      if (matched.isEmpty()) {
        break;
      }
    }

    //ACR-1edb02baa75741f6b85079c4b8b0f966
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

  /*ACR-deb90333f7b74f95b97d1101b8d0d176
ACR-1dd03075eb174eaab38e72694ea15f6e
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
