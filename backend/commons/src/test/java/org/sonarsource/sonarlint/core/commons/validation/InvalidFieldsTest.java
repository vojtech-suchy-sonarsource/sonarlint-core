/*
ACR-be93dccb58534691ba023e77da86f327
ACR-1fd63feb0c6646a6b28f5c85b9cff319
ACR-306b83dc42ea4541a84b92243b29a8be
ACR-8154f67553dc4f43a1ddb09226202a78
ACR-ed5d67ca87e04d2abfe8352e3c769cd9
ACR-acc7b85c3fd6438c849c9a3f19df3d34
ACR-9d75e661c6724285a7bc0dfca423db2d
ACR-51a5a359044146369f690b2308592c80
ACR-a7d7ca97edfe4728bb72c4d2759518d6
ACR-685dc3cc590448749f47d151591ddbac
ACR-a39f9016ee9c41f08875f1b411bed149
ACR-0b1a225e70984e4d8611a81773960f6b
ACR-f64bbc28f95b4c03ab37823d2a880c55
ACR-f1c05be4f9554847ace1e6f56bdc6927
ACR-e090481829444386ac2b106524f7b2a1
ACR-ffd06666dd1d4432acbf29ebbfd4acf1
ACR-6ff4af7fb9e9476884ccf6dd3088aa03
 */
package org.sonarsource.sonarlint.core.commons.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFieldsTest {

  public static final String[] EXPECTED = {"name1", "name2", "name3"};

  @Test
  void should_have_no_invalid_fields_initially() {
    InvalidFields tested = new InvalidFields();

    assertThat(tested.hasInvalidFields()).isFalse();
  }

  @Test
  void should_have_invalid_fields_after_adding_one() {
    InvalidFields tested = new InvalidFields();

    tested.add("name1");

    assertThat(tested.hasInvalidFields()).isTrue();
  }

  @Test
  void should_include_all_added_fields() {
    InvalidFields tested = new InvalidFields();

    tested.add("name1");
    tested.add("name2");
    tested.add("name3");
    String[] names = tested.getNames();

    assertThat(names).containsExactly(EXPECTED);
  }
}
