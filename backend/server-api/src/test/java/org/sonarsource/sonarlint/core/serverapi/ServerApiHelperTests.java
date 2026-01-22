/*
ACR-236edaa8f32b4ba7be690e0e79763b6b
ACR-b272467ac2654dbd934e3b6f8dce56ce
ACR-8ecabe54721545c0b6c46ac28c367d60
ACR-cf8ca0d868394b9eac60ce089c669d4e
ACR-2029bd3b1e9046eca0bd89e1e0efece5
ACR-33213bac4f614883b555b3e602d79fcf
ACR-65f8f121c31b43538163faafd698f1c5
ACR-d6bb0488a8ad4a0f8c80dbbe9c96040a
ACR-028b5b1dfda440a0bf2d3f492ae3fc8c
ACR-a4cbf52cecb94f8e96d3731c056abd32
ACR-fe21cce1fc0a4493a37ba5ab378da144
ACR-9ace01a50bf746a1bd5992fc97ffd1fd
ACR-a3f0f3e2cee046409382cbc884aad22e
ACR-4cde0cdff7274728abde41b6f2a49ec9
ACR-8efb20765ab94a5bb76ebf698893d6b2
ACR-9d8ffeb781544686a481f3677cb3a129
ACR-d8032f41aa91425e8655755d9c72f31a
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
