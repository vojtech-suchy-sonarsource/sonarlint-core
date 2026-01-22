/*
ACR-263444ecc3ea4a8a80326290dc5a15f1
ACR-320c656e65c3441282beae96971bf80a
ACR-0f31c0131ce7482bb388ccfdc79ddc47
ACR-7f295422d7a1469e8bad5ad6da638030
ACR-107f61c696644cf999b4ed2ecd16931b
ACR-2f2d822fdf154b3ba975d044f8d636b6
ACR-3d562f7b35b34e9bb6593050ccf416a7
ACR-f1dc45d49b69447b8cd324da7b761be6
ACR-347076c461e44864ab8702331798dd38
ACR-3dafae0608ec42ce8f713ad72d11eae8
ACR-546b8a86bd6c4efe85ffae1c650e693f
ACR-c1a0f0e0f9a8410488c1165cde733cfb
ACR-82ed68b301454e389606142be89b4363
ACR-d7ffe49ca26d4e5e9fbadd7829db5299
ACR-6278c9a9b4084ea2929207d62e852c12
ACR-465421b762a34add84279f55185dceb1
ACR-4d46cfe1bdc94a76909ba42151f04d5b
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.CheckForNull;

/*ACR-618419ce29a34505bc2913e22888fed5
ACR-0417a815e60041f596be8821f675c695
ACR-fb6d9eb265324a89a8650850692fc31e
ACR-18293dbcd31b438495da56a6ec2d25c5
ACR-55859ba1f37947eaba5c8244d553bc99
 */
public class MatchingResult<LEFT, RIGHT> {

  /*ACR-8ab925dd129541268b5055d000dbb6d3
ACR-7827b17acfc94d6298aa531e761d785f
   */
  private final IdentityHashMap<LEFT, RIGHT> leftToRight = new IdentityHashMap<>();

  private final Collection<LEFT> lefts;

  public MatchingResult(Collection<LEFT> leftIssues) {
    this.lefts = leftIssues;
  }

  /*ACR-c712d0fefc6644509671382b8c8150f5
ACR-a89cd317eabc468699072e37c31512f4
ACR-020f0b9e6388451c9e958b379460653e
ACR-0bc75806df994586ab590d4e38b9c0e7
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
