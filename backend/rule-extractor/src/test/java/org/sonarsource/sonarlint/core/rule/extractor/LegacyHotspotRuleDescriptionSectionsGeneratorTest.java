/*
ACR-b7109a00a8ea4d7d8f1aae63cba69e5b
ACR-4e896c63246e4892bd0fef22cd0119f5
ACR-e9ff6826b1de471a92678d2f61399403
ACR-70ea0339fe1a481abb6221061d2a708c
ACR-0224feecfc7a4b00a06e09fe0a9abc64
ACR-8704ece9cbe444c7ab4f81fdfc42b42b
ACR-b2a5d89702be4be8ad4dced8d727e208
ACR-048889e08bed4938b6b548db22dac42b
ACR-ebae679013ae41cdbde6d83f5bc8fb73
ACR-aa28740896544258a68cd8fdcf735e94
ACR-55c70ae60fea4fd296b4f960abd87ab8
ACR-92dc4898549c4219acb669a3f1102c75
ACR-6dbce5c218614fa4897ecfec1f75114e
ACR-8287ffc268c64c5caefa3521e7e8c11e
ACR-3bebff72cb4d442a8fbbd5ba629ef5a4
ACR-c8800e27c1b34a5f8bd1a1a426d1c783
ACR-54779b234020413799795089720745d0
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ASSESS_THE_PROBLEM_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;
import static org.sonarsource.sonarlint.core.rule.extractor.LegacyHotspotRuleDescriptionSectionsGenerator.extractDescriptionSectionsFromHtml;

/*ACR-84f1c146fe774f43a49075c0c420483f
ACR-c6a7529e737541c6955431ae45fa300b
 */
class LegacyHotspotRuleDescriptionSectionsGeneratorTest {

  /*
ACR-88f53d16431e48b4bd55d057ab07e751
   */
  private static final String DESCRIPTION =
    """
      <p>The use of operators pairs ( <code>=+</code>, <code>=-</code> or <code>=!</code> ) where the reversed, single operator was meant (<code>+=</code>,
      <code>-=</code> or <code>!=</code>) will compile and run, but not produce the expected results.</p>
      <p>This rule raises an issue when <code>=+</code>, <code>=-</code>, or <code>=!</code> is used without any spacing between the two operators and when
      there is at least one whitespace character after.</p>""";
  private static final String NON_COMPLIANT_CODE = """
    <h2>Noncompliant Code Example</h2>
    <pre>Integer target = -5;
    Integer num = 3;
    
    target =- num;  // Noncompliant; target = -3. Is that really what's meant?
    target =+ num; // Noncompliant; target = 3
    </pre>""";

  private static final String COMPLIANT_CODE =
    """
      <h2>Compliant Solution</h2>
      <pre>Integer target = -5;
      Integer num = 3;
      
      target = -num;  // Compliant; intent to assign inverse value of num is clear
      target += num;
      </pre>""";

  private static final String SEE =
    """
      <h2>See</h2>
      <ul>
        <li> <a href="https://cwe.mitre.org/data/definitions/352.html">MITRE, CWE-352</a> - Cross-Site Request Forgery (CSRF) </li>
        <li> <a href="https://www.owasp.org/index.php/Top_10-2017_A6-Security_Misconfiguration">OWASP Top 10 2017 Category A6</a> - Security
        Misconfiguration </li>
        <li> <a href="https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29">OWASP: Cross-Site Request Forgery</a> </li>
        <li> <a href="https://www.sans.org/top25-software-errors/#cat1">SANS Top 25</a> - Insecure Interaction Between Components </li>
        <li> Derived from FindSecBugs rule <a href="https://find-sec-bugs.github.io/bugs.htm#SPRING_CSRF_PROTECTION_DISABLED">SPRING_CSRF_PROTECTION_DISABLED</a> </li>
        <li> <a href="https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html#when-to-use-csrf-protection">Spring Security
        Official Documentation: When to use CSRF protection</a> </li>
      </ul>""";

  private static final String RECOMMENDED_CODING_PRACTICE =
    """
      <h2>Recommended Secure Coding Practices</h2>
      <ul>
        <li> activate Spring Security's CSRF protection. </li>
      </ul>""";

  private static final String ASK_AT_RISK =
    """
      <h2>Ask Yourself Whether</h2>
      <ul>
        <li> Any URLs responding with <code>Access-Control-Allow-Origin: *</code> include sensitive content. </li>
        <li> Any domains specified in <code>Access-Control-Allow-Origin</code> headers are checked against a whitelist. </li>
      </ul>""";

  private static final String SENSITIVE_CODE = """
    <h2>Sensitive Code Example</h2>
    <pre>
    // === Java Servlet ===
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setHeader("Content-Type", "text/plain; charset=utf-8");
      resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8080"); // Questionable
      resp.setHeader("Access-Control-Allow-Credentials", "true"); // Questionable
      resp.setHeader("Access-Control-Allow-Methods", "GET"); // Questionable
      resp.getWriter().write("response");
    }
    </pre>
    <pre>
    // === Spring MVC Controller annotation ===
    @CrossOrigin(origins = "http://domain1.com") // Questionable
    @RequestMapping("")
    public class TestController {
        public String home(ModelMap model) {
            model.addAttribute("message", "ok ");
            return "view";
        }
    
        @CrossOrigin(origins = "http://domain2.com") // Questionable
        @RequestMapping(value = "/test1")
        public ResponseEntity&lt;String&gt; test1() {
            return ResponseEntity.ok().body("ok");
        }
    }
    </pre>""";

  @Test
  void shouldReturnNoSectionForNullDescription() {
    assertThat(extractDescriptionSectionsFromHtml(null)).isEmpty();
  }

  @Test
  void shouldReturnNoSectionForEmptyDescription() {
    assertThat(extractDescriptionSectionsFromHtml("")).isEmpty();
  }

  @Test
  void parse_to_risk_description_fields_when_desc_contains_no_section() {
    var descriptionWithoutTitles = "description without titles";

    assertThat(sectionsMapFromHtml(descriptionWithoutTitles)).hasSize(1)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, descriptionWithoutTitles);
  }


  @Test
  void parse_return_null_risk_when_desc_starts_with_ask_yourself_title() {
    assertThat(sectionsMapFromHtml(ASK_AT_RISK + RECOMMENDED_CODING_PRACTICE)).hasSize(2)
      .containsEntry(ASSESS_THE_PROBLEM_SECTION_KEY, ASK_AT_RISK)
      .containsEntry(HOW_TO_FIX_SECTION_KEY, RECOMMENDED_CODING_PRACTICE);
  }


  @Test
  void parse_return_null_vulnerable_when_no_ask_yourself_whether_title() {
    assertThat(sectionsMapFromHtml(DESCRIPTION + RECOMMENDED_CODING_PRACTICE)).hasSize(2)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, DESCRIPTION)
      .containsEntry(HOW_TO_FIX_SECTION_KEY, RECOMMENDED_CODING_PRACTICE);
  }

  @Test
  void parse_return_null_fixIt_when_desc_has_no_Recommended_Secure_Coding_Practices_title() {
    assertThat(sectionsMapFromHtml(DESCRIPTION + ASK_AT_RISK)).hasSize(2)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, DESCRIPTION)
      .containsEntry(ASSESS_THE_PROBLEM_SECTION_KEY, ASK_AT_RISK);
  }

  @Test
  void parse_with_noncompliant_section_not_removed() {
    assertThat(sectionsMapFromHtml(DESCRIPTION + NON_COMPLIANT_CODE + COMPLIANT_CODE)).hasSize(3)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, DESCRIPTION)
      .containsEntry(ASSESS_THE_PROBLEM_SECTION_KEY, NON_COMPLIANT_CODE)
      .containsEntry(HOW_TO_FIX_SECTION_KEY, COMPLIANT_CODE);
  }

  @Test
  void parse_moved_noncompliant_code() {
    assertThat(sectionsMapFromHtml(DESCRIPTION + RECOMMENDED_CODING_PRACTICE + NON_COMPLIANT_CODE + SEE)).hasSize(3)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, DESCRIPTION)
      .containsEntry(ASSESS_THE_PROBLEM_SECTION_KEY, NON_COMPLIANT_CODE)
      .containsEntry(HOW_TO_FIX_SECTION_KEY, RECOMMENDED_CODING_PRACTICE + SEE);
  }

  @Test
  void parse_moved_sensitivecode_code() {
    assertThat(sectionsMapFromHtml(DESCRIPTION + ASK_AT_RISK + RECOMMENDED_CODING_PRACTICE + SENSITIVE_CODE + SEE)).hasSize(3)
      .containsEntry(ROOT_CAUSE_SECTION_KEY, DESCRIPTION)
      .containsEntry(ASSESS_THE_PROBLEM_SECTION_KEY, ASK_AT_RISK + SENSITIVE_CODE)
      .containsEntry(HOW_TO_FIX_SECTION_KEY, RECOMMENDED_CODING_PRACTICE + SEE);
  }

  private static Map<String, String> sectionsMapFromHtml(String html) {
    return extractDescriptionSectionsFromHtml(html).stream()
      .collect(Collectors.toMap(SonarLintRuleDescriptionSection::getKey, SonarLintRuleDescriptionSection::getHtmlContent));
  }
}
