/*
ACR-5eaf6ceb8996438dabe3ddd802bb6ef1
ACR-2e0a5eb00b1c4b0e82ca500bbb69b858
ACR-9980f7c13a53483ab4ae5ecea57627d6
ACR-a71a630288e5480ebb55c57ee6569e47
ACR-199e5ec107d841478b6b8c2bc2dae8c7
ACR-af14ac295c004e139c9fbacd015927e4
ACR-4ef27baf0b9746b9a78a85010ba06b6d
ACR-74747f4ae2a0427b981fbf6be53dd4a2
ACR-7b30f84919bb4c9ab6ac108b0693909f
ACR-ad25a54f8c02407b980efa5fc4a8050b
ACR-071a7e739e40494eb09ea3333165bebf
ACR-cd7c2ec1441d4f95a9abd848f8c117f1
ACR-d950d3cc10514282ac7c55c1268d4948
ACR-3acbce8896c94b9480b3711dc9e57e63
ACR-683ca752fa164d1eb5f7ff1a241eda2c
ACR-ce2768c6d5544a66acddaee18daf5985
ACR-21ea91cf2612407ca669b5c02b6f4aa6
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;

public class RequestListener implements ServletRequestListener {
  @Override
  public void requestInitialized(ServletRequestEvent sre) {
    //ACR-3008db827bb54cb7a53d95d6a2018bb7
    ((HttpServletRequest) sre.getServletRequest()).getSession();
  }

}
