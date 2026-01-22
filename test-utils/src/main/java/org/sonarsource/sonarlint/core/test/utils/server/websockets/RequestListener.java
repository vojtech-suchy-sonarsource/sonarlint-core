/*
ACR-ed065d2e87464c5080a8a9a1611b7e52
ACR-2a6ce8dd8e3f4fbab97cb0e8ea50c6d8
ACR-d66de82839684be8a0dc74b8bf8aa1e3
ACR-4c16ab660a264787bbc5eac5c57dbe80
ACR-73db79e0158144139f76ed2a757c0d54
ACR-ae06ff8ad4b748d49b04c84e27edec2c
ACR-a9b068cd39c448aa9ecbfbd2a810c318
ACR-2bd54fd4003a40e2bd77d40187eb782b
ACR-194babdd352e40a58b7ae170d3ffe633
ACR-1c82edc7b9a64c059810c31ae98b5e3b
ACR-559173fdb1f148dd948b16af45082306
ACR-234c52e4f39648769eb2b05e73f286f7
ACR-38b3cb12593e4101b8ec8278e0ef2681
ACR-f5f5125e08ca48d69dbc439b97a8dc60
ACR-5c0bda24b0ed4ec4ba16a9cc1e633663
ACR-8a537172b4b84b8fb31cf6433cf48cb7
ACR-a60a3dd2d44d4f9b85784946d9abca2a
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;

public class RequestListener implements ServletRequestListener {
  @Override
  public void requestInitialized(ServletRequestEvent sre) {
    //ACR-32aea56b7d224d56b17e1cade48e6aca
    ((HttpServletRequest) sre.getServletRequest()).getSession();
  }

}
