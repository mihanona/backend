package com.mihanona.backend.shared.sequence;

import com.mihanona.backend.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class SequenceService {

    private final DocumentSequenceRepository repository;

    /**
     * Returns the next document number (e.g. "DEV-2026-0012"), safely,
     * even under concurrent requests — the row is locked (FOR UPDATE)
     * for the duration of this method's transaction, so two simultaneous
     * calls can never receive the same number.
     *
     * If no sequence row exists yet for this tenant/type/year, creates one
     * automatically, starting at 1 — this is what makes the very FIRST
     * quote a brand-new tenant creates "just work," with no manual setup.
     */
    @Transactional
    public String getNextNumber(String documentType, String defaultPrefix) {
        int currentYear = Year.now().getValue();
        var tenantId = TenantContext.get();

        DocumentSequence sequence = repository
                .findForUpdate(tenantId, documentType, currentYear)
                .orElseGet(() -> {
                    DocumentSequence newSeq = new DocumentSequence();
                    newSeq.setTenantId(tenantId);
                    newSeq.setDocumentType(documentType);
                    newSeq.setYear(currentYear);
                    newSeq.setPrefix(defaultPrefix);
                    newSeq.setNextNumber(1);
                    return newSeq;
                });

        int numberToUse = sequence.getNextNumber();
        sequence.setNextNumber(numberToUse + 1);
        repository.save(sequence);

        // e.g. "DEV-2026-0001"
        return String.format("%s-%d-%04d", sequence.getPrefix(), currentYear, numberToUse);
    }
}