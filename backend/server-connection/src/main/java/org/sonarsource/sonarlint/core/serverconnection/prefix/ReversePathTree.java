/*
ACR-2d31b64c52644445bddbf89869928935
ACR-2ca99121d2ce4f0e85a49b96fb6da91f
ACR-ddc411e0f1de45b6b0a1986196c14173
ACR-4a7c1ab790114dc197f235ebb33c9256
ACR-afd396214ad844389876f879698b62e8
ACR-0c62165fa0e64b609d58f41d4c8d5be8
ACR-1628a71514744b67842c7b225313056d
ACR-9532c4aa85904d53a1b00dd7dfa82fc2
ACR-91575a0a9c854d8c88117f155ab65d13
ACR-60be96f4d4ef451eb3c8c3f15845015e
ACR-4aa6d820e88a4c8389c65ecb6408f8be
ACR-21a7f627704f45c2bd6ed04e8e793f27
ACR-9e5b5ee097604c46ae7f1a4f0b5b4208
ACR-04c2aa8e18ec46d9ba38f63ac75c4d2f
ACR-63b565a588304c5386f9837849df41e0
ACR-3735efbd807c4b42a7e48ed763345e67
ACR-e5145dc5194b4dcd83924fb4cb764995
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

  /*ACR-eab100110a5e4d5592e58d127f0e5ac9
ACR-bcd3cd9654884833b034141879cbf1a3
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
