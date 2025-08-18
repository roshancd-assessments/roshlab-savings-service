package com.roshlab.savings.service;

import com.roshlab.savings.exception.OffensiveNicknameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class NickNameValidatorTest {

    private NickNameValidatorService validatorEnabled;
    private NickNameValidatorService validatorDisabled;

    private static final String NICKNAME_WORDS_LIST = "fool\nidot\nterrible";
    private static final Resource NICKNAME_RESOURCE = new ByteArrayResource(NICKNAME_WORDS_LIST.getBytes());

    @BeforeEach
    void setUp() throws IOException {
        // Instantiate the validator with the nickname check enabled
        validatorEnabled = new NickNameValidatorService(true, NICKNAME_RESOURCE);
        validatorEnabled.loadNicknameWords();

        // Instantiate the validator with the nickname check disabled
        validatorDisabled = new NickNameValidatorService(false, NICKNAME_RESOURCE);
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
        assertThrows(OffensiveNicknameException.class, () -> validatorEnabled.validate(invalidNickname));
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
