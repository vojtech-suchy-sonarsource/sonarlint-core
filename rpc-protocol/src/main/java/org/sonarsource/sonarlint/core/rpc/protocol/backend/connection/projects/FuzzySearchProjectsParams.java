/*
ACR-053d7d549aec489991dab6a0e9000010
ACR-6a66982858684fcfac4c6a86953552c1
ACR-9da92f20ea384b90967d07a70485ce47
ACR-c84ae727f70f4b9f8d5cc89ec1ccce1a
ACR-bf420ecf278342e2bf3c0988efd8b398
ACR-8fc1e7bc1d1e4dc593918dff85977343
ACR-e60d046b44fc432e8aa9df6ad20ef80e
ACR-6ccd4c27b5de4814a59db615e1acb30c
ACR-325c6fc04bf1465ab64864e961d3712c
ACR-1f5ff1bb402544d5ad98b5033c1834d4
ACR-9d8a440e5bbf45c68e23e7d6d76200a6
ACR-cdfc8d88491f4a9c8b40762d47befb7f
ACR-ed05a0f4b5a14545921ee18bf5cb9711
ACR-c98de7c57f0d4c23b615f87db53f8809
ACR-efbd09a92613468298d6166ea0588d16
ACR-1bb261496db54e53a434b13a341c159f
ACR-a2bf5f583d9c4d2b8aa7cfd19064ac3d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

public class FuzzySearchProjectsParams {

  private final String connectionId;
  private final String searchText;

  public FuzzySearchProjectsParams(String connectionId, String searchText) {
    this.connectionId = connectionId;
    this.searchText = searchText;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSearchText() {
    return searchText;
  }
}
