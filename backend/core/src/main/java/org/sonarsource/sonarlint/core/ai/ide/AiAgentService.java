/*
ACR-92b077bcfa0d4dc488c8f699bbf5021e
ACR-ae7e685d8eb44499b3c0ecbaeb29c53d
ACR-c3e43e5061914755879707a86c004555
ACR-7f297d9809104018aa98a30ba87f59e9
ACR-66a58aa563ee429e857ae926ab223556
ACR-64a382363d5b4a6a97372f8853106b31
ACR-5b164e3c1f42413fb868698e0e37cdd0
ACR-1fa87f15e8bb4cd9b15bbb08b02cfbf3
ACR-1a03b29a4e664b9098a357d6703e7f0e
ACR-91d0a3187e7249e8a61a5d607360cba3
ACR-76ef71b9be2c4f2398b71205ed79518f
ACR-4c040cd3ad634aed9fd1e830e016b3c1
ACR-e66c3c14c54f4bb6bacf741610eaa4ab
ACR-2b64d4be98ba4ee9bd22319f60e8b031
ACR-4da4c8bee3d949ddb44840c9d6af37cc
ACR-522cbd5cb77743a7b7fe0f53180374ae
ACR-79b70dd1e2354478952f07a26a396bf9
 */
package org.sonarsource.sonarlint.core.ai.ide;

import jakarta.inject.Inject;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetRuleFileContentResponse;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

public class AiAgentService {

  private final TelemetryService telemetryService;

  @Inject
  public AiAgentService(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  public GetRuleFileContentResponse getRuleFileContent(AiAgent agent) {
    var header = switch (agent) {
      case CURSOR, WINDSURF -> """
        ---
        description: SonarQube MCP Server usage guidelines
        globs:
        alwaysApply: true
        ---

        """;
      case KIRO -> """
        ---
        inclusion: always
        ---

        """;
      case GITHUB_COPILOT -> """
        ---
        applyTo: "**/*"
        ---

        """;
    };
    var response = new GetRuleFileContentResponse(header
      + """
        These are some guidelines when using the SonarQube MCP server.

        # Important Tool Guidelines

        ## Basic usage
        - **IMPORTANT**: After you finish generating or modifying any code files at the very end of the task, you MUST call the `analyze_file_list` \
        tool (if it exists) to analyze the files you created or modified.
        - **IMPORTANT**: When starting a new task, you MUST disable automatic analysis with the `toggle_automatic_analysis` tool if it exists.
        - **IMPORTANT**: When you are done generating code at the very end of the task, \
        you MUST re-enable automatic analysis with the `toggle_automatic_analysis` tool if it exists.

        ## Project Keys
        - When a user mentions a project key, use `search_my_sonarqube_projects` first to find the exact project key
        - Don't guess project keys - always look them up

        ## Code Language Detection
        - When analyzing code snippets, try to detect the programming language from the code syntax
        - If unclear, ask the user or make an educated guess based on syntax

        ## Branch and Pull Request Context
        - Many operations support branch-specific analysis
        - If user mentions working on a feature branch, include the branch parameter

        ## Code Issues and Violations
        - After fixing issues, do not attempt to verify them using `search_sonar_issues_in_projects`, as the server will not yet reflect the updates

        # Common Troubleshooting

        ## Authentication Issues
        - SonarQube requires USER tokens (not project tokens)
        - When the error `SonarQube answered with Not authorized` occurs, verify the token type

        ## Project Not Found
        - Use `search_my_sonarqube_projects` to find available projects
        - Verify project key spelling and format

        ## Code Analysis Issues
        - Ensure programming language is correctly specified
        - Remind users that snippet analysis doesn't replace full project scans
        - Provide full file content for better analysis results
        """);

    telemetryService.mcpRuleFileRequested();

    return response;
  }
}
