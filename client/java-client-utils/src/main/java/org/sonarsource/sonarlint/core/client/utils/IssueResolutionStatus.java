/*
ACR-35942519de2346989b200ef420380e76
ACR-4349185c3dcf442481e257f5f2987d6c
ACR-5bea9cc9137d4b73a6973a59d66d73cb
ACR-74be9d0d2484499fae9d5c4cf49a7179
ACR-73b2dac18de84e27a4f180c639ea883b
ACR-c79da56fb83843208368633374def97a
ACR-9e95ef82b6bf46ca9b4825767d7904ff
ACR-67123855924d4638889e71ee7f40197d
ACR-c661d055ddc04f0d9d28625a43835951
ACR-3b21ab327eb3442d84059b99723302ae
ACR-19a9524e24aa489c8a1c0164c837fdc7
ACR-0564d6001a97482fb81c662e72a7b5ce
ACR-83811d227f3142c79031a2eb799bf27d
ACR-1cfe5275aed44357862e5876453e5c20
ACR-bd84c7e3e58e41fa86f23b451bb1574b
ACR-8ed24a18b1e641a5b975283344549796
ACR-9a0d1936d72542f18ecb1a4dd0d6bb68
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;

public enum IssueResolutionStatus {
  ACCEPT("Accepted", "The issue is valid but will not be fixed now. It represents accepted technical debt."),
  WONT_FIX("Won't Fix", "The issue is valid but does not need fixing. It represents accepted technical debt."),
  FALSE_POSITIVE("False Positive", "The issue is raised unexpectedly on code that should not trigger an issue.");

  private final String title;
  private final String description;

  IssueResolutionStatus(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public static IssueResolutionStatus fromDto(ResolutionStatus status) {
    switch (status) {
      case ACCEPT:
        return ACCEPT;
      case WONT_FIX:
        return WONT_FIX;
      case FALSE_POSITIVE:
        return FALSE_POSITIVE;
      default:
        throw new IllegalArgumentException("Unknown status: " + status);
    }
  }
}
