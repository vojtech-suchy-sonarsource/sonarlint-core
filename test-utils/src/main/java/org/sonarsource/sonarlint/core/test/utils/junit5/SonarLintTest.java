/*
ACR-c5e9581aa0514565bd83c5be63bf4fd9
ACR-ef7133b8a1664b2db801d978382953f8
ACR-af2562e92cf24800a3e34bf7539b6b44
ACR-01f70700a15e4d6aa271c3407c3d0215
ACR-34cb3cf6f98949ac9e9b8568de4d1d87
ACR-3ac9b8aaf1d843eeaf90ae684845377a
ACR-66420d8a99c444f8b49be81d0a1a2b7f
ACR-c32bd63011b5444e8b7181326f5a9b6d
ACR-1ecef47e37d64f86a18faff4ac94ffb4
ACR-0f6bd2c0599b4ac7956c5b8fc4d3d587
ACR-01cfe2d2ca2245828e933309dc53e882
ACR-b67713af4abd443388b6dd18b994c47f
ACR-3bc859d624a04830b95769f216941b36
ACR-9ab3a6863ede4912812a16b43539cb73
ACR-2a427706f3b94bb6b7d6019fa8ba8b74
ACR-3dd8b284b5ad4dabbef09a9e05d085a1
ACR-d1d552d0dd454851a82d474623fb7f58
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
