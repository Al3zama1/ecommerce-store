package com.abranlezama.ecommercestore.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class USPostalCodeValidatorTest {

    private USPostalCodeValidator cut;

    @BeforeEach
    void setUp() {
        this.cut = new USPostalCodeValidator();
    }


    @Test
    void shouldAcceptValidFiveDigitPostalCode() {
        // Given
        String postalCode = "90055";

        // When
        boolean valid = cut.isValid(postalCode, null);

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    void shouldAcceptValidTenDigitPostalCode() {
        // Given
        String postalCode = "90055-4444";

        // When
        boolean valid = cut.isValid(postalCode, null);

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    void shouldNotAcceptPostalCodeWithLessThanFiveDigits() {
        // Given
        String postalCode = "9005";

        // When
        boolean valid = cut.isValid(postalCode, null);

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    void shouldNotAcceptPostalCodeWithMoreThanFiveAndLessThanTenDigits() {
        // Given
        String postalCode = "90055-454";

        // When
        boolean valid = cut.isValid(postalCode, null);

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    void shouldNotAcceptPostalCodeWithNineDigitsAndNotDash() {
        // Given
        String postalCode = "900554549";

        // When
        boolean valid = cut.isValid(postalCode, null);

        // Then
        assertThat(valid).isFalse();
    }

}
