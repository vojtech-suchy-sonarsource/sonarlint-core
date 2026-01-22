/*
ACR-25b68387375d440db12bcb8b79f59f93
ACR-938d1c4420f3488a8f723b0cfca59ec8
ACR-088c7b274e6c48fcaf83b1df3432a707
ACR-0db0ce0b62c544e69779152ec13c84aa
ACR-f19447f9dd7d4cc5a33c04d79aa92522
ACR-81b6910a3295450896b82b6dc121ba21
ACR-5094ece81ae74073b6884930eab41803
ACR-a22f4240c14a4937ab3f0f72023f7118
ACR-2b11f358b4d94a79ba64e7c75d03582b
ACR-4dfe5c06a4134bddb65f1232dc3bc039
ACR-25266bb96e9a43489dd3ccaf3497f6b5
ACR-f7eea201378a4751948bc41ac669b125
ACR-aad77823fbc24ab09f4a71f72fcfa0a0
ACR-0146b5ca0cd9421ab16a346069a10c94
ACR-4438e26a06b44073ad574cc3c071d29b
ACR-7613f19d9fc7433b9c4dc5528920816d
ACR-276763cef82742c3b9a60e1baa981219
 */
package org.sonarsource.sonarlint.core.telemetry;

public class ToolCallCounter {
  private int success;
  private int error;

  public ToolCallCounter() {
  }

  public ToolCallCounter(int success, int error) {
    this.success = success;
    this.error = error;
  }

  public void incrementCount(boolean succeeded) {
    if (succeeded) {
      success++;
    } else {
      error++;
    }
  }

  public int getSuccess() {
    return success;
  }

  public int getError() {
    return error;
  }
}
