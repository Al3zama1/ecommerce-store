package com.abranlezama.ecommercestore.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class USPhoneValidatorTest {

    private USPhoneValidator cut;

    @BeforeEach
    void setUp() {
        this.cut = new USPhoneValidator();
    }

    @Test
    void shouldApproveValidUSPhoneNumber() {
        // Given
        String phone = "232-450-3333";

        // When
        boolean isValid = cut.isValid(phone, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldRejectValidUSPhoneThatIsNotInCorrectFormat() {
        // Given
        String phone = "232 450-3333";

        // When
        boolean isValid = cut.isValid(phone, null);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectValidUSPhoneThatHasParenthesis() {
        // Given
        String phone = "(232)-450-3333";

        // When
        boolean isValid = cut.isValid(phone, null);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectNonUSNumber() {
        // Given
        String phone = "132-450-3333";

        // When
        boolean isValid = cut.isValid(phone, null);

        assertThat(isValid).isFalse();
    }

}
