/*
ACR-7c63786125344cbdb1bd87094ea5f80a
ACR-32c2b7a565f042f3a17ac3c03828c581
ACR-1ab9f025f94c47f1a29d49554270da68
ACR-dacf9967c0ee4387885cb760dbc6674c
ACR-e9429149b819405397ebd871a61b0bb1
ACR-c6cdfe3ec33b45289bf679bdfd76d163
ACR-0ced874f24854ed0943391d29a8a6e04
ACR-eea3a72e8ff6462aa11217d0a85a9279
ACR-6cc622216d5c4fc28b778f6e0c062af3
ACR-f7cf19781d5d4a62bc5113dee9b566e6
ACR-31623d7a9e33401c913409a78ea9fccf
ACR-74d920c8942c45ccae3c08c6219920b0
ACR-0f39e8d032e84cf4b0a375cf5ce72088
ACR-46f81abdab3b437d81457df062eceac6
ACR-d8c31b40fa3b4eea900d051d4b5e5782
ACR-59aaf6299e80416f9792d027bc0656ad
ACR-11ecf3c3853b485288693b3ef5997080
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.CheckForNull;

/*ACR-0040042a21a7469186aa7de3ca5fc6c9
ACR-12e58f38747047668ec1390b4f565e57
ACR-108b6c4704244170ae037b581828e7d0
ACR-75572f32decd4458a1de05e3526776ba
ACR-cbe8e9a1f48143cf8afabab7b5059fec
 */
public class MatchingResult<LEFT, RIGHT> {

  /*ACR-4b4b9093285f46a481a85b0526f807c9
ACR-b83edca4f0ec48dd8b0593a6d8d32c17
   */
  private final IdentityHashMap<LEFT, RIGHT> leftToRight = new IdentityHashMap<>();

  private final Collection<LEFT> lefts;

  public MatchingResult(Collection<LEFT> leftIssues) {
    this.lefts = leftIssues;
  }

  /*ACR-65a70aca9c474176aa3eea3c5e2fad92
ACR-9a5af2bd2b054f088f65cf5b0631974a
ACR-e6ddc316762e4723980b728f9e6193c9
ACR-aae63b561f6641f3bf91785208d5988a
   */
  public Iterable<LEFT> getUnmatchedLefts() {
    List<LEFT> result = new ArrayList<>();
    for (LEFT left : lefts) {
      if (!leftToRight.containsKey(left)) {
        result.add(left);
      }
    }
    return result;
  }

  public Map<LEFT, RIGHT> getMatchedLefts() {
    return leftToRight;
  }

  void recordMatch(LEFT left, RIGHT right) {
    leftToRight.put(left, right);
  }

  boolean isComplete() {
    return leftToRight.size() == lefts.size();
  }

  @CheckForNull
  public RIGHT getMatch(LEFT left) {
    return leftToRight.get(left);
  }

  public Optional<RIGHT> getMatchOpt(LEFT left) {
    return Optional.ofNullable(getMatch(left));
  }

}
