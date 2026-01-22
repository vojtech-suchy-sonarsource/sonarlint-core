/*
ACR-57aff3524c4441a3ace5b967e9f39c79
ACR-379e0e6864e54e37a268f85da77d3e4f
ACR-0acde88e071143ffbbfc6eb03e48d581
ACR-099d9e505de44f3db9768e67dd7df36e
ACR-7a56117d32234f829dd3c9cfe376d43d
ACR-adb5f95f5fe94f73b7ad4204c1cfd167
ACR-98ec2ab762164189b54e28c73ec5af70
ACR-d09da94137374ff187500b5ee1b4872d
ACR-5a15d9f216b84205ac9170f15708f26d
ACR-0d884d98797a423e8ba28d6ac3856ee3
ACR-c4d6ae2161b94235bfd7e00be0f5dccd
ACR-b5a2a0854419427dbb6afb945333ef0e
ACR-06e94632119e48058b9166fde6a367de
ACR-419ee9207cfa42a8bb7f13b34f90a48e
ACR-5709ea6d48a448698d0023608eeef2db
ACR-e27eb9758c75462eb3a83359b22e2910
ACR-1143506dca5546fd8009843be27bba26
 */
package org.sonarsource.sonarlint.core.serverapi;

import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.serverapi.exception.ForbiddenException;
import org.sonarsource.sonarlint.core.serverapi.exception.NotFoundException;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerErrorException;
import org.sonarsource.sonarlint.core.serverapi.exception.TooManyRequestsException;
import org.sonarsource.sonarlint.core.serverapi.exception.UnauthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerApiHelperTests {

  @Test
  void concat_should_handle_base_url_with_trailing_slash() {
    var result = ServerApiHelper.concat("http://localhost:9000/", "/api/test");
    
    assertThat(result).isEqualTo("http://localhost:9000/api/test");
  }

  @Test
  void concat_should_handle_base_url_without_trailing_slash() {
    var result = ServerApiHelper.concat("http://localhost:9000", "/api/test");
    
    assertThat(result).isEqualTo("http://localhost:9000/api/test");
  }

  @Test
  void concat_should_handle_relative_path_without_leading_slash() {
    var result = ServerApiHelper.concat("http://localhost:9000", "api/test");
    
    assertThat(result).isEqualTo("http://localhost:9000/api/test");
  }

  @Test
  void concat_should_handle_empty_relative_path() {
    var result = ServerApiHelper.concat("http://localhost:9000", "");
    
    assertThat(result).isEqualTo("http://localhost:9000/");
  }

  @Test
  void handleError_should_throw_unauthorized_exception() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error)
      .isInstanceOf(UnauthorizedException.class)
      .hasMessage("Not authorized. Please check server credentials.");
  }

  @Test
  void handleError_should_throw_forbidden_exception() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
    when(response.bodyAsString()).thenReturn("{\"errors\":[{\"msg\":\"Access denied\"}]}");

    var error = ServerApiHelper.handleError(response);

    assertThat(error)
      .isInstanceOf(ForbiddenException.class)
      .hasMessage("Access denied");
  }

  @Test
  void handleError_should_throw_forbidden_exception_with_default_message() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
    when(response.bodyAsString()).thenReturn("{}");

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error)
      .isInstanceOf(ForbiddenException.class)
      .hasMessage("Access denied");
  }

  @Test
  void handleError_should_throw_not_found_exception() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
    when(response.url()).thenReturn("http://localhost:9000/api/test");

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error).isInstanceOf(NotFoundException.class);
  }

  @Test
  void handleError_should_throw_server_error_exception() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
    when(response.url()).thenReturn("http://localhost:9000/api/test");

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error).isInstanceOf(ServerErrorException.class);
  }

  @Test
  void handleError_should_throw_too_many_requests_exception() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(ServerApiHelper.HTTP_TOO_MANY_REQUESTS);

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error)
      .isInstanceOf(TooManyRequestsException.class)
      .hasMessage("Too many requests have been made.");
  }

  @Test
  void handleError_should_throw_illegal_state_exception_for_other_codes() {
    var response = mock(HttpClient.Response.class);
    when(response.code()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
    when(response.url()).thenReturn("http://localhost:9000/api/test");
    when(response.bodyAsString()).thenReturn("{\"errors\":[{\"msg\":\"Bad request\"}]}");

    var error = ServerApiHelper.handleError(response);
    
    assertThat(error)
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Error 400 on http://localhost:9000/api/test");
  }

} 
