package com.mihanona.backend.shared.sequence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, UUID> {

    // @Lock + PESSIMISTIC_WRITE is Hibernate's way of writing SELECT ... FOR UPDATE.
    // "Pessimistic" means: assume conflict WILL happen, lock defensively upfront —
    // as opposed to "optimistic" locking, which assumes conflicts are rare and
    // only checks for them after the fact. For sequence numbers, pessimistic
    // is the correct, safe choice — we cannot risk a duplicate number.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM DocumentSequence s WHERE s.tenantId = :tenantId AND s.documentType = :type AND s.year = :year")
    Optional<DocumentSequence> findForUpdate(UUID tenantId, String type, int year);
}