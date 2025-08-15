package com.roshlab.savings.service;

import com.roshlab.savings.exception.ProfanityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NickNameValidator {

    private final boolean nicknameCheckEnabled;
    private final Resource nicknameFileResource;
    private Set<String> nicknameWords;

    public NickNameValidator(@Value("${feature.nicknameCheck:false}") boolean nicknameCheckEnabled,
                             @Value("classpath:${validation.nickname.file}") Resource nicknameFileResource) {
        this.nicknameCheckEnabled = nicknameCheckEnabled;
        this.nicknameFileResource = nicknameFileResource;
        log.info("Nickname check enabled: {}", nicknameCheckEnabled);
        log.info("Loading nickname words from {}", nicknameFileResource);
    }

    @PostConstruct
    public void loadNicknameWords() {
        if (!nicknameCheckEnabled) {
            log.info("nickname check is disabled. Skipping nickname file loading.");
            this.nicknameWords = Set.of();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(nicknameFileResource.getInputStream()))) {
            this.nicknameWords = reader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            log.info("Loaded {} nickname words from file.", nicknameWords.size());
        } catch (Exception exception) {
            log.error("Failed to load nickname words from file: {}", nicknameFileResource.getFilename(), exception);
            throw new IllegalStateException("Could not load nickname words.", exception);
        }
    }

    // Validates the given nickname against the loaded nickname words.
    public void validate(String nickname) {
        // precondition: nickname check is enabled
        if (!nicknameCheckEnabled) {
            log.info("nickname check is disabled. Skipping validation for nickname: {}", nickname);
            return;
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            return;
        }

        String lowerCaseNickname = nickname.toLowerCase();
        for (String word : nicknameWords) {
            if (lowerCaseNickname.contains(word)) {
                log.warn("nickname found in nickname: '{}'", nickname);
                throw new ProfanityException("Nickname contains nickname.");
            }
        }
    }
}