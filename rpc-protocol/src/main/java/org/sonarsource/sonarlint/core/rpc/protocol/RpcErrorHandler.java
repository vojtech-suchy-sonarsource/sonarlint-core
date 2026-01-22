/*
ACR-ac717c9b59f24231bb264f70d4459aaf
ACR-c1f35f8abd804bf781442efef12f8e7c
ACR-83ebdf4359bd4f689997b335c0fcc375
ACR-c5e1d358a8684925a06dd9bc3bef674d
ACR-2752253956254a3dad5a898d667ac1e6
ACR-94f7e3f59f3f43969a25d11ff189e5d8
ACR-f203e58830764722b01d0fc72a0c4535
ACR-14d6614d3032411b81eb4ca2838d5807
ACR-363fe76117fb458289116d90f89c3ffb
ACR-ba7563782c62477299e8c8e9db072702
ACR-eeae093e9bff473f876f0af9706e76b9
ACR-b731ace23de44a2c8114c8fe7d6b7437
ACR-cab62c2870ff4c78b5f88f6decf50ab4
ACR-ab7ca6671d854dd4b12f7c2397b075e4
ACR-48f6b98a3087428a91aca631020d592a
ACR-3b4cc9d51401403aa9fefa9022127dbc
ACR-ae19bc46618a4be99f5e9e1996542df3
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import io.sentry.Attachment;
import io.sentry.Hint;
import io.sentry.Sentry;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;

public class RpcErrorHandler {

  private RpcErrorHandler() {
  }

  public static ResponseError handleError(Throwable throwable) {
    if (throwable instanceof ResponseErrorException) {
      return ((ResponseErrorException) throwable).getResponseError();
    } else if (isWrappedResponseErrorException(throwable)) {
      return ((ResponseErrorException) throwable.getCause()).getResponseError();
    } else {
      return createInternalErrorResponse("Internal error", throwable);
    }
  }

  private static boolean isWrappedResponseErrorException(Throwable throwable) {
    return (throwable instanceof CompletionException || throwable instanceof InvocationTargetException)
      && throwable.getCause() instanceof ResponseErrorException;
  }

  public static ResponseError createInternalErrorResponse(String header, Throwable throwable) {
    var error = new ResponseError();
    error.setMessage(header + ".");
    error.setCode(ResponseErrorCode.InternalError);
    var stackTraceString = toStringStacktrace(throwable);

    //ACR-7fe1b4ef2f644872aec401dc5c79bcf2
    var stackTraceAttachment = new Attachment(stackTraceString.getBytes(StandardCharsets.UTF_8), "stacktrace.txt");
    Sentry.captureException(throwable, Hint.withAttachment(stackTraceAttachment));

    error.setData(stackTraceString);
    return error;
  }

  private static String toStringStacktrace(Throwable throwable) {
    var stackTrace = new ByteArrayOutputStream();
    var stackTraceWriter = new PrintWriter(stackTrace);
    throwable.printStackTrace(stackTraceWriter);
    stackTraceWriter.flush();
    return stackTrace.toString(StandardCharsets.UTF_8);
  }
}
