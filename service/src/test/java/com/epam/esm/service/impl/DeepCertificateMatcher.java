package com.epam.esm.service.impl;

import com.epam.esm.entity.Certificate;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class DeepCertificateMatcher implements ArgumentMatcher<Certificate> {
    private final Certificate certificateToMatch;

    public DeepCertificateMatcher(Certificate certificateToMatch) {
        this.certificateToMatch = certificateToMatch;
    }

    @Override
    public boolean matches(Certificate argument) {
        return Objects.equals(certificateToMatch.getId(), argument.getId()) &&
                Objects.equals(certificateToMatch.getName(), argument.getName()) &&
                Objects.equals(certificateToMatch.getDescription(), argument.getDescription()) &&
                Objects.equals(certificateToMatch.getPrice(), argument.getPrice()) &&
                Objects.equals(certificateToMatch.getDuration(), argument.getDuration()) &&
                Objects.equals(certificateToMatch.getTags(), argument.getTags());
    }
}
