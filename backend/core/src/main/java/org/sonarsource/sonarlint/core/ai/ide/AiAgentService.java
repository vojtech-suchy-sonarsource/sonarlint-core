/*
ACR-2bc91dd0f37a40c5a85a07cfc1773b96
ACR-aea7800b0ae942d4b21435bfe62f18d7
ACR-e5acd6fed38246fd89dbe4c18fba8f4b
ACR-e156ec4f2f924720a3ea9933f0aed8cc
ACR-0b890a74760d4aa39fddf9056b61f65d
ACR-e7b2e3b009dd4641a58befc30630d4ae
ACR-7305fff2e5414e6ba82c7bcdfd4abf39
ACR-2b8754977a6443439dd9f7bb2181ff61
ACR-c2f272f84c1143d58a33bcc83757d174
ACR-4dc24e4d03fb4f25bdf8983b9ed9bfe8
ACR-811f6eb8d48d41689a0911a9bbf3eebd
ACR-b63bd4b4c4b4463391ec76cbc45143ba
ACR-2aef879fbf4a4a01bba19f6fdd8ff7f3
ACR-4e5d81c883ac41589321559bf286969b
ACR-298eb5cff9064cc983f04589645acf01
ACR-3a9afb55b7b74fbb824d3e2c6da242af
ACR-c9bfd376efc24ffb99e172fc3074bd4f
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
