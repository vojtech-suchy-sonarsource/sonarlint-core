/*
ACR-360715da968d4cb89a82401fafd360db
ACR-7877f442614c41109a948ff468e22a26
ACR-f88a2b5b56284697abfa6291b50530dc
ACR-16accd7a6a284e728775c9bdb2e62be5
ACR-a074b7956a654f94a82fcd1b7a7d0b8c
ACR-fa03d5cdcd0a44c79d2aee7d375f5f23
ACR-fd4d929fb5474bcc81819ff02120eff4
ACR-8c323bc2cc1348f6bcbbdb14b21726fb
ACR-f3f3a074823f40968e9c90ca673a0d1d
ACR-bde52c48126d4350831ab131e3e5a867
ACR-dcd4af327c2a4935bbe56e6cbf233556
ACR-88ec0c9e7fdd4467880246a2ebad4ee3
ACR-dcff678322f34f47ae6de01f48f4facf
ACR-b195bc63900a434d810a53d3db26310a
ACR-0b6c5c610a6445b799646be6a25b02ca
ACR-b0258bc45dd34b529318288e2df9fa67
ACR-75d0ed616cbf46e8a7e20769429a0db3
 */
package org.sonarsource.sonarlint.core.http;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;

class RedirectInterceptor implements HttpResponseInterceptor {

  @Override
  public void process(HttpResponse response, EntityDetails entity, HttpContext context) {
    alterResponseCodeIfNeeded(context, response);
  }

  private static void alterResponseCodeIfNeeded(HttpContext context, HttpResponse response) {
    if (isPost(context)) {
      //ACR-bd5b4c38d13d47d385b015959cf3feae
      //ACR-fcb56a36160649e09f9e9679185fbfbd
      var code = response.getCode();
      if (code == HttpStatus.SC_MOVED_PERMANENTLY) {
        response.setCode(HttpStatus.SC_PERMANENT_REDIRECT);
      } else if (code == HttpStatus.SC_MOVED_TEMPORARILY || code == HttpStatus.SC_SEE_OTHER) {
        response.setCode(HttpStatus.SC_TEMPORARY_REDIRECT);
      }
    }
  }

  private static boolean isPost(HttpContext context) {
    var httpCoreContext = HttpCoreContext.cast(context);
    var request = httpCoreContext.getRequest();
    return request != null && Method.POST.isSame(request.getMethod());
  }
}
