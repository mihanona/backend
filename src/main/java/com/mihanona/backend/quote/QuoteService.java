package com.mihanona.backend.quote;

import com.mihanona.backend.catalog.CatalogItem;
import com.mihanona.backend.catalog.CatalogItemRepository;
import com.mihanona.backend.client.ClientRepository;
import com.mihanona.backend.quote.dto.*;
import com.mihanona.backend.shared.security.TenantContext;
import com.mihanona.backend.shared.sequence.SequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteItemRepository quoteItemRepository;
    private final ClientRepository clientRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final SequenceService sequenceService;

    @Transactional
    public QuoteResponse create(QuoteRequest request) {
        UUID tenantId = TenantContext.get();

        // Ownership check, same pattern as CatalogItem->CatalogCategory
        clientRepository.findByIdAndTenantId(request.getClientId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Quote quote = new Quote();
        quote.setTenantId(tenantId);
        quote.setClientId(request.getClientId());
        quote.setPropertyId(request.getPropertyId());
        quote.setTitle(request.getTitle());
        quote.setExpiryDate(request.getExpiryDate());
        quote.setIssueDate(LocalDate.now());
        quote.setQuoteNumber(sequenceService.getNextNumber("QUOTE", "DEV"));

        BigDecimal subtotal = BigDecimal.ZERO;
        List<QuoteItem> items = new java.util.ArrayList<>();

        for (QuoteItemRequest itemRequest : request.getItems()) {
            QuoteItem item = new QuoteItem();
            item.setTenantId(tenantId);
            item.setQuantity(itemRequest.getQuantity());
            item.setDiscountPct(itemRequest.getDiscountPct());
            item.setOptional(itemRequest.isOptional());

            if (itemRequest.getCatalogItemId() != null) {
                // Pulling from the catalog — SNAPSHOT its current name/price
                // right now. If the catalog price changes next month, this
                // quote_item keeps today's price forever, unaffected.
                CatalogItem catalogItem = catalogItemRepository
                        .findByIdAndTenantId(itemRequest.getCatalogItemId(), tenantId)
                        .orElseThrow(() -> new IllegalArgumentException("Catalog item not found"));

                item.setCatalogItemId(catalogItem.getId());
                item.setItemType(catalogItem.getType().name().toLowerCase());
                item.setItemName(catalogItem.getName());
                item.setUnitPrice(catalogItem.getUnitPrice());
            } else {
                // Custom, off-catalog line — no catalog_item_id, uses
                // whatever the client typed directly.
                if (itemRequest.getCustomName() == null || itemRequest.getCustomUnitPrice() == null) {
                    throw new IllegalArgumentException("Custom items require a name and price");
                }
                item.setItemType("service");
                item.setItemName(itemRequest.getCustomName());
                item.setUnitPrice(itemRequest.getCustomUnitPrice());
            }

            // lineTotal = quantity * unitPrice * (1 - discountPct/100)
            BigDecimal discountMultiplier = BigDecimal.ONE
                    .subtract(item.getDiscountPct().divide(new BigDecimal("100")));
            BigDecimal lineTotal = item.getQuantity()
                    .multiply(item.getUnitPrice())
                    .multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            subtotal = subtotal.add(lineTotal);
            items.add(item);
        }

        BigDecimal taxAmount = subtotal
                .multiply(quote.getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        quote.setSubtotal(subtotal);
        quote.setTaxAmount(taxAmount);
        quote.setTotalAmount(subtotal.add(taxAmount));

        Quote savedQuote = quoteRepository.save(quote);

        items.forEach(item -> item.setQuoteId(savedQuote.getId()));
        List<QuoteItem> savedItems = quoteItemRepository.saveAll(items);

        return QuoteResponse.of(savedQuote, savedItems.stream().map(QuoteItemResponse::from).toList());
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> listAll() {
        return quoteRepository.findByTenantId(TenantContext.get()).stream()
                .map(quote -> QuoteResponse.of(quote,
                        quoteItemRepository.findByQuoteId(quote.getId()).stream()
                                .map(QuoteItemResponse::from).toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteResponse getOne(UUID id) {
        Quote quote = quoteRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));
        List<QuoteItemResponse> items = quoteItemRepository.findByQuoteId(quote.getId())
                .stream().map(QuoteItemResponse::from).toList();
        return QuoteResponse.of(quote, items);
    }

    @Transactional
    public QuoteResponse updateStatus(UUID id, String newStatus) {
        Quote quote = quoteRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

        // A small, explicit map of what's allowed FROM each current status.
        // This is a real business rule, not just data validation — it belongs
        // here in the service layer, not left to chance in the frontend.
        boolean validTransition = switch (quote.getStatus()) {
            case "draft" -> newStatus.equals("sent");
            case "sent" -> newStatus.equals("approved") || newStatus.equals("declined");
            default -> false; // approved/declined are final states — no further changes
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Cannot change quote status from '" + quote.getStatus() + "' to '" + newStatus + "'");
        }

        quote.setStatus(newStatus);
        Quote saved = quoteRepository.save(quote);

        List<QuoteItemResponse> items = quoteItemRepository.findByQuoteId(saved.getId())
                .stream().map(QuoteItemResponse::from).toList();
        return QuoteResponse.of(saved, items);
    }
}