/*
ACR-1a62df140153477aad71886e01637bba
ACR-1d275aef1cd84d8286d61ee8b5a2cb67
ACR-82e52cab52114131b5173f461c0d895d
ACR-50137d1f006c4577991a1856e2712b1a
ACR-754fc3b484194b75a6f872ba19cbdfd9
ACR-609c7976fc834ffca994899129bd980b
ACR-4504a650e736402aa13652dd50558f67
ACR-1e4d984ce0c54255bf281aca77420bee
ACR-d15b9693c7054721ab0d5144fb09b712
ACR-7c396a25e4a848e98ad6277250ebd2dd
ACR-78aefe65546c4ae5bd6bdf0d0216ac6b
ACR-8110eb8f3e74458e996d1cead063ceca
ACR-b397efd7e1bb41d9b735e7dfac13124f
ACR-231289471ddf4c20801cd503bb2e6340
ACR-d9d9d5958458419db0e706bcc3b0d41f
ACR-8909c0245ac54d888609dd1993047866
ACR-e3ea3617efd947578c1f313c3612daad
 */
package org.sonarsource.sonarlint.core.serverconnection.prefix;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

class ReversePathTree {
  private final Node root = new MultipleChildrenNode();

  public void index(Path path) {
    Node parent = null;
    var currentNode = root;
    Path currentNodePath = null;

    for (var i = path.getNameCount() - 1; i >= 0; i--) {
      var childNodePath = path.getName(i);
      var result = currentNode.computeChildrenIfAbsent(parent, currentNodePath, childNodePath);
      parent = result[0];
      currentNode = result[1];
      currentNodePath = childNodePath;
    }

    currentNode.setTerminal(true);
  }

  public Match findLongestSuffixMatches(Path path) {
    var currentNode = root;
    var matchLen = 0;

    while (matchLen < path.getNameCount()) {
      var nextEl = path.getName(path.getNameCount() - matchLen - 1);
      var nextNode = currentNode.getChild(nextEl);
      if (nextNode == null) {
        break;
      }
      matchLen++;
      currentNode = nextNode;
    }

    return collectAllPrefixes(currentNode, matchLen);
  }

  private static Match collectAllPrefixes(Node node, int matchLen) {
    List<Path> paths = new ArrayList<>();
    if (matchLen > 0) {
      collectPrefixes(node, Paths.get(""), paths);
    }
    return new Match(paths, matchLen);
  }

  private static void collectPrefixes(Node node, Path currentPath, List<Path> paths) {
    if (node.isTerminal()) {
      paths.add(currentPath);
    }

    for (Map.Entry<Path, Node> child : node.childrenEntrySet()) {
      var childPath = child.getKey().resolve(currentPath);
      collectPrefixes(child.getValue(), childPath, paths);
    }
  }

  /*ACR-2d84934a83dc4fb5a95423046e15bfc0
ACR-e56f30745a6b42a5a83dbe20096cc930
   */
  private interface Node {
    Node[] computeChildrenIfAbsent(Node parent, Path currentNodePath, Path childNodePath);

    Set<Map.Entry<Path, Node>> childrenEntrySet();

    Node getChild(Path name);

    void setTerminal(boolean b);

    boolean isTerminal();

    void put(Path path, Node node);
  }

  private abstract static class AbstractNode implements Node {
    private boolean terminal;

    @Override
    public final boolean isTerminal() {
      return terminal;
    }

    @Override
    public final void setTerminal(boolean b) {
      this.terminal = b;
    }
  }

  private static class SingleChildNode extends AbstractNode {
    @Nullable
    private Path singleChildKey;
    @Nullable
    private Node singleChildValue;

    @Override
    public Node[] computeChildrenIfAbsent(Node parent, Path currentNodePath, Path childNodePath) {
      if (singleChildKey == null) {
        put(childNodePath, new SingleChildNode());
        return new Node[] {this, singleChildValue};
      }
      if (childNodePath.equals(singleChildKey)) {
        return new Node[] {this, singleChildValue};
      }
      var child = new SingleChildNode();
      var replacement = new MultipleChildrenNode();
      replacement.put(singleChildKey, singleChildValue);
      replacement.put(childNodePath, child);
      parent.put(currentNodePath, replacement);
      return new Node[] {replacement, child};
    }

    @Override
    public Set<Map.Entry<Path, Node>> childrenEntrySet() {
      if (singleChildKey == null) {
        return Collections.emptySet();
      } else {
        return Collections.singleton(new AbstractMap.SimpleEntry<>(singleChildKey, singleChildValue));
      }
    }

    @Override
    public void put(Path path, Node node) {
      this.singleChildKey = path;
      this.singleChildValue = node;
    }

    @Override
    @CheckForNull
    public Node getChild(Path name) {
      return name.equals(singleChildKey) ? singleChildValue : null;
    }
  }

  private static class MultipleChildrenNode extends AbstractNode {

    private final Map<Path, Node> children = new HashMap<>();

    @Override
    public Node[] computeChildrenIfAbsent(Node parent, Path currentNodePath, Path childNodePath) {
      return new Node[] {this, children.computeIfAbsent(childNodePath, e -> new SingleChildNode())};
    }

    @Override
    public Set<Map.Entry<Path, Node>> childrenEntrySet() {
      return children.entrySet();
    }

    @CheckForNull
    @Override
    public Node getChild(Path name) {
      return children.get(name);
    }

    @Override
    public void put(Path path, Node node) {
      children.put(path, node);
    }

  }

  public static class Match {
    private final List<Path> paths;
    private final int matchLen;

    private Match(List<Path> paths, int matchLen) {
      this.paths = paths;
      this.matchLen = matchLen;
    }

    public List<Path> matchPrefixes() {
      return paths;
    }

    public int matchLen() {
      return matchLen;
    }

  }
}
