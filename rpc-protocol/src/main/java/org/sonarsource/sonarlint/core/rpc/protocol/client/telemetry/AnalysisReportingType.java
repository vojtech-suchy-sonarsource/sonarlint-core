/*
ACR-f1ce84c37ad9425a803710b88c3b2ba2
ACR-26a316195d8b46a69e45f2303d0cc4af
ACR-c72a7a6d340c4ceb8661c976f7ca9424
ACR-ad82b15dadeb49bda4dce7de8f95aa45
ACR-121145b231ff4d5ebb2452de5341b89e
ACR-0a0288a738d14b19ba0917522c6e89df
ACR-04784e3d51664480a73221b6a5a00b47
ACR-7a625b5133ca4ecca9be3629380198aa
ACR-b4adb624fcb844aeb03bf29c38acd015
ACR-1c96e7be12c042088f567dfbe174336a
ACR-4be35f3519bf472c96fba4c88e8b8b37
ACR-fb2f0c7d31cb4c6d89c4643b98f27ae0
ACR-c5ec0fb73bb34051a3c3df725a3b1e4c
ACR-41b29073a1444f0faaef2dbe4f2ff1a3
ACR-7294e2a6c6db441bacaa4b5694b6795a
ACR-c5ba7c35588948ea9fd70739fa6ea865
ACR-92e0c7248f034455962e5c0c73c5549f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public enum AnalysisReportingType {
  VCS_CHANGED_ANALYSIS_TYPE("trigger_count_vcs_changed_files"),
  ALL_FILES_ANALYSIS_TYPE("trigger_count_all_project_files"),
  PRE_COMMIT_ANALYSIS_TYPE("trigger_count_pre_commit"),
  CURRENT_FILE_ANALYSIS_TYPE("trigger_count_current_file_ignoring_exclusions"),
  WHOLE_FOLDER_HOTSPOTS_SCAN_TYPE("trigger_count_whole_folder_hotspots_scan");

  private final String id;

  AnalysisReportingType(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
