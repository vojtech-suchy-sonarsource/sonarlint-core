/*
ACR-96b3ee638cc3408bac2106275ceb2558
ACR-e40de818bc4b44d68998c48681977e2c
ACR-1ace9963547349ac8f2b0c52bb2cc200
ACR-7fd88a1817784b25b2ff342bc91309fc
ACR-360eb1d49bc7485cb87a8b4733323941
ACR-8291d919078d46efa9efac4c5a208de2
ACR-f9e236ea9edf4185836b9d10688b4e36
ACR-de91b112b0db4863bd47c85411586fa0
ACR-ea06cf09eba94428ae4ed6c390e389eb
ACR-5148e8c707904855bac657d32987c1f2
ACR-6e0b1bf6f5464cf9b42262cea20d16be
ACR-bb9834957ae94a3390bf92aa395165a8
ACR-30bde92062074ad783972447cc0bc6a3
ACR-a6b75c40eef645fb8855fb73451503d3
ACR-1eed055e6d564cea9fc6c994bbf24b05
ACR-7d69df9f3c4144f8aac0421b1a904eec
ACR-7ef5ba96be174c678e406a153019d70b
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

/*ACR-f3fce9854a4247a19b3949961214b099
ACR-a47b8a2ff9b7475e88a0df2fc3cafed3
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

  /*ACR-30910bace30b41dcaeb6aeb64b877869
ACR-162c56475221492f97f9d885d23cc34f
   */
  public HttpClient.Response rawGetAnonymous(String relativePath, SonarLintCancelMonitor cancelMonitor) {
    return rawGetUrlAnonymous(buildEndpointUrl(relativePath), cancelMonitor);
  }

  /*ACR-7e92860356b64184b6382a4abe89e76c
ACR-99aa81bc2bb74441a0dbd905554b041d
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
        //ACR-b940b693cf7b4460b21ed1b342d7fed9
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
    //ACR-af34214f290c475eb6918a41a264619c
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
