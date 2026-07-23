package com.mihanona.backend.quote;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, UUID> {
    List<QuoteItem> findByQuoteId(UUID quoteId);
}