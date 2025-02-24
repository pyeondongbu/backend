package com.pyeon.domain.auth.validator;

import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2Validator {

    private static final String GOOGLE = "google";

    public void validateRegistrationId(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!GOOGLE.equals(registrationId)) {
            throw new CustomException(
                    ErrorCode.OAUTH2_REGISTRATION_NOT_FOUND
            );
        }
    }

    public void validateEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null) {
            throw new CustomException(
                    ErrorCode.OAUTH2_EMAIL_NOT_FOUND
            );
        }

        if (!Boolean.TRUE.equals(attributes.get("email_verified"))) {
            throw new CustomException(
                    ErrorCode.OAUTH2_EMAIL_NOT_VERIFIED
            );
        }
    }

    public void validateAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new CustomException(
                    ErrorCode.OAUTH2_USER_INFO_NOT_FOUND
            );
        }
    }
}