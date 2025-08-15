package com.roshlab.savings.service;

import com.roshlab.savings.exception.ProfanityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class NickNameValidatorTest {

    private NickNameValidator validatorEnabled;
    private NickNameValidator validatorDisabled;

    private static final String NICKNAME_WORDS_LIST = "fool\nidot\nterrible";
    private static final Resource NICKNAME_RESOURCE = new ByteArrayResource(NICKNAME_WORDS_LIST.getBytes());

    @BeforeEach
    void setUp() throws IOException {
        // Instantiate the validator with the nickname check enabled
        validatorEnabled = new NickNameValidator(true, NICKNAME_RESOURCE);
        validatorEnabled.loadNicknameWords();

        // Instantiate the validator with the nickname check disabled
        validatorDisabled = new NickNameValidator(false, NICKNAME_RESOURCE);
        validatorDisabled.loadNicknameWords();
    }

    @Test
    void validate_shouldPass_whenNicknameIsCleanAndCheckIsEnabled() {
        String cleanNickname = "MySavings";
        assertDoesNotThrow(() -> validatorEnabled.validate(cleanNickname));
    }

    @Test
    void validate_shouldThrowException_whenNicknameContainsInvalidAndCheckIsEnabled() {
        String invalidNickname = "AFoolAccount";
        assertThrows(ProfanityException.class, () -> validatorEnabled.validate(invalidNickname));
    }

    @Test
    void validate_shouldPass_whenNicknameContainsInvalidButCheckIsDisabled() {
        String profaneNickname = "myIdiotAccount";
        assertDoesNotThrow(() -> validatorDisabled.validate(profaneNickname));
    }

    @Test
    void validate_shouldPass_whenNicknameIsNull() {
        assertDoesNotThrow(() -> validatorEnabled.validate(null));
    }

    @Test
    void validate_shouldPass_whenNicknameIsEmpty() {
        assertDoesNotThrow(() -> validatorEnabled.validate(""));
    }
}
