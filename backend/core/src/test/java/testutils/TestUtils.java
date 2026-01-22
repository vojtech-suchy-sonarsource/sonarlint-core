/*
ACR-97248a897b6a49d488658716b5ef308e
ACR-c4f0cc9e264a4c3895413ec75c31f989
ACR-a6443d2063c34ce5a76e637e2818be81
ACR-e13c27c25e9c418d812006fbbada75de
ACR-9f09eca6f21c475896e4d69e2d511543
ACR-4496ce019d12409189945786d323c1cc
ACR-7d5a99cb18d4404a8b4811ab9ffb37b2
ACR-718e87ccee4f4d1aba8b5c2147712971
ACR-e242d5464d2a415e806b75bb010b8275
ACR-23c71be950ef43fa95ae20ca4406ad8c
ACR-4c15ac7d1d1f4bb292a5d1af47f5b532
ACR-1805ad35d89c45e58dc980c2b0007b77
ACR-a6df8e726589402fb772fbd2848a5dbe
ACR-f0afd0a16df14510a4e289578cf54f0b
ACR-f8149886e8774916b78090468f186955
ACR-a31ea57703e04744aed030fc2544bb99
ACR-84f89ee318de4f12bcfe3bd0518a1ce3
 */
package testutils;

import java.lang.management.ManagementFactory;

public class TestUtils {

  private static String generateThreadDump() {
    final var dump = new StringBuilder();
    final var threadMXBean = ManagementFactory.getThreadMXBean();
    final var threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
    for (var threadInfo : threadInfos) {
      dump.append('"');
      dump.append(threadInfo.getThreadName());
      dump.append("\" ");
      final var state = threadInfo.getThreadState();
      dump.append("\n   java.lang.Thread.State: ");
      dump.append(state);
      final var stackTraceElements = threadInfo.getStackTrace();
      for (final var stackTraceElement : stackTraceElements) {
        dump.append("\n        at ");
        dump.append(stackTraceElement);
      }
      dump.append("\n\n");
    }
    return dump.toString();
  }

  public static void printThreadDump() {
    System.out.println(generateThreadDump());
  }
}
