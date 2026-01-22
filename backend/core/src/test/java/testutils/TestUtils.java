/*
ACR-8a434c0360244a1aa9969d417f709f72
ACR-c6401429fa69482daca2ff9dca3a683a
ACR-f2053bca7215498c990a74bde82a269c
ACR-57ed44f52c6c4130ac9ec6470daadae5
ACR-d9c70400813540be8d88a26813bc58ca
ACR-bc69db0964454818990d0e7eb785021e
ACR-fe55274fdeae4e5ea7c73ac25fb5b5e8
ACR-c8b19158f63142bc96867f653aabc5f0
ACR-54cc677a33254141b0a7f9a4dcf0e1c7
ACR-67d8c70a3d534281bca5833fd014ad77
ACR-f8f2b23245224fe4b940d5113159f374
ACR-4dcd718995b24e9ab2a913d31d98a1c5
ACR-ff97f770a2ea439cb63041ceca94be35
ACR-16f0fbfb3d6745aa862a167a8812db71
ACR-a898f6bdabeb4c7586de213510202407
ACR-e735ba632cba440fac59bd39e8c677e0
ACR-33b29c0466244c54acd7ad0f10af9f66
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
