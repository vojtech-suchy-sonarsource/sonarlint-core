/*
ACR-e2554db70c044f919753983080fc4543
ACR-defd614f439547adb6da0b5c382f2df2
ACR-7a88ba3b7f9d4da385fadac06c955259
ACR-0b4599dc1c594c06bc97e32c70a221ae
ACR-cbf845701ada411b87cfcfd3552bf71c
ACR-3c4a70d31ec64793886cc19edf714bf5
ACR-06f1c080d20d4f34be2c42db2ff175bd
ACR-506aa9f0a1194bb89a54d93b1100ec8e
ACR-7c2aeb0e78f54e76ae4dd338cdcb51e9
ACR-d626b12c61a243deb49228cf8ef2e963
ACR-4c14578f9fe04c1cbdf171aab53651f2
ACR-a838f814eeca4b21b999fe1a120749d7
ACR-b3ebf17e6256437eb44cbaf12577fad3
ACR-5c5c6d44e92c47be9855ca688114b8d2
ACR-bae82e4db30542d7837ec4f1189ed658
ACR-4d5fff81610f4b8ebaf97745cd076480
ACR-ad59222267c541988f4420f199e76525
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

    //ACR-80c30401893845fbb9ae8ae60608d3c0
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
