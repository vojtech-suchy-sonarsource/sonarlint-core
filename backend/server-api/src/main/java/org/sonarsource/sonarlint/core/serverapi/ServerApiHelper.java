/*
ACR-74a60988e06543579b636c9074c00f8b
ACR-ca9694d4f8124ef9b3c6c8b178b032f4
ACR-f118f5f3c48a46fa95bfb7f5797f47da
ACR-b30540e9754f4f439aea87ea7218b35b
ACR-00d3df56de854346b5e7407223a0d485
ACR-761426528c5a4f74b28bea7e779ab392
ACR-f3352410d2094619bcf86e838c190a1d
ACR-ee30a41896604dba9f7840ac611e8efe
ACR-8db8e0b940f049499fde710585543eab
ACR-13eb82d97b0f4f8290faebb7beb6cc6c
ACR-c1c0adb443b74e8b8c604d01b089ea4a
ACR-7a1eb1f4666b451fb55516d45e8f7cfb
ACR-1ecc69ee0aa74b808326b5b59275c855
ACR-fe0cbb9b2df04464a8b2da0823e3ac91
ACR-fd2594f5620d4d43a39fbd7860ccb268
ACR-2ecf0f19235a4a5ebbbe3cab5d4516f3
ACR-cf400d2408fc4e808746a8f4cbedd597
 */
package org.sonarsource.sonarlint.core.serverapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpConnectionListener;
import org.sonarsource.sonarlint.core.serverapi.exception.ForbiddenException;
import org.sonarsource.sonarlint.core.serverapi.exception.NetworkException;
import org.sonarsource.sonarlint.core.serverapi.exception.NotFoundException;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerErrorException;
import org.sonarsource.sonarlint.core.serverapi.exception.TooManyRequestsException;
import org.sonarsource.sonarlint.core.serverapi.exception.UnauthorizedException;

import static java.util.Objects.requireNonNull;

/*ACR-312865f355714c4d8a807ce57597cb38
ACR-804435822c9244439391e20d6dafc68c
 */
public class ServerApiHelper {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static final int PAGE_SIZE = 500;
  public static final int MAX_PAGES = 20;
  public static final int HTTP_TOO_MANY_REQUESTS = 429;

  private final HttpClient client;
  private final EndpointParams endpointParams;

  public ServerApiHelper(EndpointParams endpointParams, HttpClient client) {
    this.endpointParams = endpointParams;
    this.client = client;
  }

  public boolean isSonarCloud() {
    return endpointParams.isSonarCloud();
  }

  public HttpClient.Response getAnonymous(String path, SonarLintCancelMonitor cancelMonitor) {
    var response = rawGetAnonymous(path, cancelMonitor);
    if (!response.isSuccessful()) {
      throw handleError(response);
    }
    return response;
  }

  public HttpClient.Response get(String path, SonarLintCancelMonitor cancelMonitor) {
    var response = rawGet(path, cancelMonitor);
    if (!response.isSuccessful()) {
      throw handleError(response);
    }
    return response;
  }

  public HttpClient.Response apiGet(String path, SonarLintCancelMonitor cancelMonitor) {
    var response = rawGetUrl(buildApiEndpointUrl(path), cancelMonitor);
    if (!response.isSuccessful()) {
      throw handleError(response);
    }
    return response;
  }

  public HttpClient.Response post(String relativePath, String contentType, String body, SonarLintCancelMonitor cancelMonitor) {
    return postUrl(buildEndpointUrl(relativePath), contentType, body, cancelMonitor);
  }

  public HttpClient.Response apiPost(String relativePath, String contentType, String body, SonarLintCancelMonitor cancelMonitor) {
    return postUrl(buildApiEndpointUrl(relativePath), contentType, body, cancelMonitor);
  }

  private HttpClient.Response postUrl(String url, String contentType, String body, SonarLintCancelMonitor cancelMonitor) {
    var response = rawPost(url, contentType, body, cancelMonitor);
    if (!response.isSuccessful()) {
      throw handleError(response);
    }
    return response;
  }

  /*ACR-e3028485abeb4e2aa4772deeae1be68d
ACR-54f6ad3d685b48b59e216a0d2a124970
   */
  public HttpClient.Response rawGetAnonymous(String relativePath, SonarLintCancelMonitor cancelMonitor) {
    return rawGetUrlAnonymous(buildEndpointUrl(relativePath), cancelMonitor);
  }

  /*ACR-69b3f72c818d4e12b7a49b957bfe8f74
ACR-dc1bbcac96aa42e787bab1e4e57005db
   */
  public HttpClient.Response rawGet(String relativePath, SonarLintCancelMonitor cancelMonitor) {
    return rawGetUrl(buildEndpointUrl(relativePath), cancelMonitor);
  }

  private HttpClient.Response rawGetUrl(String url, SonarLintCancelMonitor cancelMonitor) {
    var startTime = Instant.now();
    var httpFuture = client.getAsync(url);
    return processResponse("GET", cancelMonitor, httpFuture, startTime, url);
  }

  private HttpClient.Response rawGetUrlAnonymous(String url, SonarLintCancelMonitor cancelMonitor) {
    var startTime = Instant.now();
    var httpFuture = client.getAsyncAnonymous(url);
    return processResponse("GET", cancelMonitor, httpFuture, startTime, url);
  }

  public HttpClient.Response rawPost(String url, String contentType, String body, SonarLintCancelMonitor cancelMonitor) {
    var startTime = Instant.now();
    var httpFuture = client.postAsync(url, contentType, body);
    return processResponse("POST", cancelMonitor, httpFuture, startTime, url);
  }

  private static HttpClient.Response processResponse(String method, SonarLintCancelMonitor cancelMonitor, CompletableFuture<HttpClient.Response> httpFuture,
    Instant startTime, String url) {
    cancelMonitor.onCancel(() -> httpFuture.cancel(true));
    try {
      var response = httpFuture.join();
      logTime(method, startTime, url, response.code());
      return response;
    } catch (Exception e) {
      logFailure(method, startTime, url, e.getMessage());
      throw new NetworkException("Request failed", e);
    }
  }

  private static void logTime(String method, Instant startTime, String url, int responseCode) {
    var duration = Duration.between(startTime, Instant.now());
    LOG.debug("{} {} {} | response time={}ms", method, responseCode, url, duration.toMillis());
  }

  private static void logFailure(String method, Instant startTime, String url, String message) {
    var duration = Duration.between(startTime, Instant.now());
    LOG.debug("{} {} {} | failed after {}ms", method, url, message, duration.toMillis());
  }

  private String buildEndpointUrl(String relativePath) {
    return concat(endpointParams.getBaseUrl(), relativePath);
  }

  private String buildApiEndpointUrl(String relativePath) {
    return concat(requireNonNull(endpointParams.getApiBaseUrl()), relativePath);
  }

  public static String concat(String baseUrl, String relativePath) {
    return Strings.CS.appendIfMissing(baseUrl, "/") +
      (relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
  }

  public static RuntimeException handleError(HttpClient.Response toBeClosed) {
    try (var failedResponse = toBeClosed) {
      if (failedResponse.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
        return new UnauthorizedException("Not authorized. Please check server credentials.");
      }
      if (failedResponse.code() == HttpURLConnection.HTTP_FORBIDDEN) {
        //ACR-d677d3c956f346d79d2e8fa722965706
        var error = tryParseAsJsonError(failedResponse);
        if (error == null) {
          error = "Access denied";
        }
        return new ForbiddenException(error);
      }
      if (failedResponse.code() == HttpURLConnection.HTTP_NOT_FOUND) {
        return new NotFoundException(formatHttpFailedResponse(failedResponse, null));
      }
      if (failedResponse.code() >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
        return new ServerErrorException(formatHttpFailedResponse(failedResponse, null));
      }
      if (failedResponse.code() == HTTP_TOO_MANY_REQUESTS) {
        return new TooManyRequestsException("Too many requests have been made.");
      }

      var errorMsg = tryParseAsJsonError(failedResponse);

      return new IllegalStateException(formatHttpFailedResponse(failedResponse, errorMsg));
    }
  }

  private static String formatHttpFailedResponse(HttpClient.Response failedResponse, @Nullable String errorMsg) {
    return "Error " + failedResponse.code() + " on " + failedResponse.url() + (errorMsg != null ? (": " + errorMsg) : "");
  }

  @CheckForNull
  private static String tryParseAsJsonError(HttpClient.Response response) {
    var content = response.bodyAsString();
    if (StringUtils.isBlank(content)) {
      return null;
    }
    var obj = JsonParser.parseString(content).getAsJsonObject();
    var errors = obj.getAsJsonArray("errors");
    if (errors == null) {
      return null;
    }
    List<String> errorMessages = new ArrayList<>();
    for (JsonElement e : errors) {
      errorMessages.add(e.getAsJsonObject().get("msg").getAsString());
    }
    return String.join(", ", errorMessages);
  }

  public Optional<String> getOrganizationKey() {
    return endpointParams.getOrganization();
  }

  public <G, F> void getPaginated(String relativeUrlWithoutPaginationParams, CheckedFunction<InputStream, G> responseParser, Function<G, Number> getPagingTotal,
    Function<G, List<F>> itemExtractor, Consumer<F> itemConsumer, boolean limitToTwentyPages, SonarLintCancelMonitor cancelChecker) {
    getPaginated(relativeUrlWithoutPaginationParams, responseParser, getPagingTotal, itemExtractor, itemConsumer, limitToTwentyPages, cancelChecker, "p", "ps");
  }

  public <G, F> void getPaginated(String relativeUrlWithoutPaginationParams, CheckedFunction<InputStream, G> responseParser, Function<G, Number> getPagingTotal,
    Function<G, List<F>> itemExtractor, Consumer<F> itemConsumer, boolean limitToTwentyPages, SonarLintCancelMonitor cancelChecker, String pageFieldName,
    String pageSizeFieldName) {
    var baseUrl = buildEndpointUrl(relativeUrlWithoutPaginationParams);
    getPaginatedBaseUrl(baseUrl, responseParser, getPagingTotal, itemExtractor,
      itemConsumer, limitToTwentyPages, cancelChecker, pageFieldName,
      pageSizeFieldName);
  }

  public <G, F> void apiGetPaginated(String relativeUrlWithoutPaginationParams, CheckedFunction<InputStream, G> responseParser, Function<G, Number> getPagingTotal,
    Function<G, List<F>> itemExtractor, Consumer<F> itemConsumer, boolean limitToTwentyPages, SonarLintCancelMonitor cancelChecker, String pageFieldName,
    String pageSizeFieldName) {
    var baseUrl = buildApiEndpointUrl(relativeUrlWithoutPaginationParams);
    getPaginatedBaseUrl(baseUrl, responseParser, getPagingTotal, itemExtractor,
      itemConsumer, limitToTwentyPages, cancelChecker, pageFieldName,
      pageSizeFieldName);
  }

  private <G, F> void getPaginatedBaseUrl(String baseUrl, CheckedFunction<InputStream, G> responseParser, Function<G, Number> getPagingTotal,
    Function<G, List<F>> itemExtractor, Consumer<F> itemConsumer, boolean limitToTwentyPages, SonarLintCancelMonitor cancelChecker, String pageFieldName,
    String pageSizeFieldName) {
    var page = new AtomicInteger(0);
    var stop = new AtomicBoolean(false);
    var loaded = new AtomicInteger(0);
    do {
      page.incrementAndGet();
      var fullUrl = baseUrl + (baseUrl.contains("?") ? "&" : "?") +
        pageSizeFieldName + "=" + PAGE_SIZE + "&" + pageFieldName + "=" + page;
      ServerApiHelper.consumeTimed(
        () -> rawGetUrl(fullUrl, cancelChecker),
        response -> processPage(baseUrl, responseParser, getPagingTotal, itemExtractor, itemConsumer, limitToTwentyPages, page, stop, loaded,
          response),
        duration -> LOG.debug("Page downloaded in {}ms", duration));
    } while (!stop.get() && !cancelChecker.isCanceled());
  }

  private static <F, G> void processPage(String baseUrl, CheckedFunction<InputStream, G> responseParser, Function<G, Number> getPagingTotal, Function<G, List<F>> itemExtractor,
    Consumer<F> itemConsumer, boolean limitToTwentyPages, AtomicInteger page, AtomicBoolean stop, AtomicInteger loaded,
    HttpClient.Response response)
    throws IOException {
    if (!response.isSuccessful()) {
      throw handleError(response);
    }
    G protoBufResponse;
    try (var body = response.bodyAsStream()) {
      protoBufResponse = responseParser.apply(body);
    }

    var items = itemExtractor.apply(protoBufResponse);
    for (F item : items) {
      itemConsumer.accept(item);
      loaded.incrementAndGet();
    }
    var isEmpty = items.isEmpty();
    var pagingTotal = getPagingTotal.apply(protoBufResponse).longValue();
    //ACR-5049972c6dda4ebeae7a80ba501cf05e
    stop.set(isEmpty || (pagingTotal > 0 && (long) page.get() * PAGE_SIZE >= pagingTotal));
    if (!stop.get() && limitToTwentyPages && page.get() >= MAX_PAGES) {
      stop.set(true);
      LOG.debug("Limiting number of requested pages from '{}' to {}. Some of the data won't be fetched", baseUrl, MAX_PAGES);
    }
  }

  public HttpClient.AsyncRequest getEventStream(String path, HttpConnectionListener connectionListener, Consumer<String> messageConsumer) {
    return client.getEventStream(buildEndpointUrl(path),
      connectionListener,
      messageConsumer);
  }

  @FunctionalInterface
  public interface CheckedFunction<T, R> {
    R apply(T t) throws IOException;
  }

  public static <G> G processTimed(Supplier<HttpClient.Response> responseSupplier, IOFunction<HttpClient.Response, G> responseProcessor,
    LongConsumer durationConsumer) {
    var startTime = Instant.now();
    G result;
    try (var response = responseSupplier.get()) {
      result = responseProcessor.apply(response);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to parse WS response: " + e.getMessage(), e);
    }
    durationConsumer.accept(Duration.between(startTime, Instant.now()).toMillis());
    return result;
  }

  public static void consumeTimed(Supplier<HttpClient.Response> responseSupplier, IOConsumer<HttpClient.Response> responseConsumer,
    LongConsumer durationConsumer) {
    processTimed(responseSupplier, r -> {
      responseConsumer.accept(r);
      return null;
    }, durationConsumer);
  }

  @FunctionalInterface
  public interface IOFunction<T, R> {
    R apply(T t) throws IOException;
  }

  @FunctionalInterface
  public interface IOConsumer<T> {
    void accept(T t) throws IOException;
  }

}
