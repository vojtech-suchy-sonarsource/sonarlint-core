/*
ACR-0552e7ba813e44e59ca49d3cbdb61e45
ACR-8387694b25434276abe6723db31d6fb7
ACR-dceb533d2fa34da48cae78b1c12f4eed
ACR-36b420a3e70a488b88733bc6f72a2d52
ACR-39f33a3329d84c6395b54c6cbee1fa3a
ACR-d25def173533439ebe37ac5c2f871296
ACR-8cb729c1d964440780b3f639718e781b
ACR-645c89a080c44b55bd4276e7acc099ab
ACR-41c840d1da36436ba6c69c0d3f9f17b9
ACR-383c20f815794d9dac4ce26c3c1eb09d
ACR-5e87c4cda3c14806b8daf1ec538fe0f6
ACR-02ab105f26ba4eceb229d9d77ebaaf49
ACR-b048c3d88c134b19a7d6280d489185b6
ACR-251dbd1386b1478aa371f68ac1914697
ACR-e71e8b9acf07471ba3fea3c8b03f3d9e
ACR-13b84b3521cf44bbadf34870921715bd
ACR-d3b448d4f49d4558a7547170c5f41c7d
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
