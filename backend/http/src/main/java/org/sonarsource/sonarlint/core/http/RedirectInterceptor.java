/*
ACR-8588525769f44fadb0dc437ac4753d18
ACR-d95575a83ebb4a7b9921561485febd77
ACR-e2b788aba4514661b1c30dbed3d4e957
ACR-a715fc9210fd478e87427343b3468399
ACR-5bb9d2e417f74f94a1d5ccafb0c78cac
ACR-edd7fa1f75e14989be9add3423d1385b
ACR-477bc545f84746c2bd970001c154db83
ACR-1e2295d4537a436b9a5e683a2f88315b
ACR-22d003434853446b8a1301a3a07ce1fa
ACR-b85f79d2e46849c3b8f63c63c2eb134d
ACR-565c149681384e5fb52ecd920ff1b5e7
ACR-d1f85eae9c614d4ba4a20b2de354faab
ACR-202ad9649ef14126a8436e128cad6fcc
ACR-8a64940f9bdc4fa4bdf03e8d69bf3db3
ACR-6cffc49880f14e0d8d2ff1bd97125bb8
ACR-50e5f39234474c3b91bc3e663ecaa584
ACR-7cb0c5bfe59e4356b6b2183b567eae64
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
      //ACR-3d5bad5a25684e55aaae6100ee1469a8
      //ACR-9f405029438d477eb44f8e3c6dfbd165
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
