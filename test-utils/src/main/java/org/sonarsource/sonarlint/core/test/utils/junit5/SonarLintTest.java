/*
ACR-e5281351381c4139989a81b3077c068d
ACR-7c959928355845509fe19865ee3aede8
ACR-a0ed476bd2e4463da17aa972248de5ce
ACR-028934b4eee24f4fa482b456b5eb033f
ACR-7f530d62ff324946b66735d51b34f748
ACR-9f5a814d833f48fe8f839c4f85dff8d2
ACR-fc2757bb9b894dc990fe00e750cbaeed
ACR-3801d9f387074ccda4c8c3abf910c3b7
ACR-7630094992614b79a08192d616eb2923
ACR-2ff04183ad574e22a27b3864c18df486
ACR-1c738ff9e0d34aa69df4a9b9f07dccf7
ACR-11d2f181edfc493db264e27452349763
ACR-c19c98e4703f45ffb61ee44195e4ff80
ACR-a4e17a44e7164146b88f8d76c7448c83
ACR-dbcd4890996641ed9dd0f3634a23f6cb
ACR-9785c19f0b914d6e887e2ea8dd73ea42
ACR-eaa217dade8b45f199d9d2a73e26ed20
 */
package org.sonarsource.sonarlint.core.test.utils.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Test
@ExtendWith(SonarLintTestHarness.class)
public @interface SonarLintTest {
}
