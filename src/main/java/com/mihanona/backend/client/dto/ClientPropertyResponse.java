package com.mihanona.backend.client.dto;

import com.mihanona.backend.client.ClientProperty;
import lombok.Getter;

import java.util.UUID;

/**
 * WHAT THIS IS:
 * The shape of data we send back to the frontend whenever it asks about
 * a client's property (a physical location — a house, office, worksite).
 *
 * WHY IT EXISTS (separate from the ClientProperty entity):
 * Same reasoning as UserResponse hiding passwordHash — this DTO controls
 * exactly what the API exposes. Right now it looks similar to the entity,
 * but this separation matters going forward: if ClientProperty later gets
 * an internal-only field (e.g. GPS coordinates for route optimization we
 * don't want shown yet, or an internal access code), we add it to the
 * entity WITHOUT automatically leaking it here — we'd have to deliberately
 * add it to this class too. The entity and the API response are allowed
 * to diverge on purpose.
 *
 * RELATIONSHIP TO CLIENT:
 * One Client can have MANY properties (client ||──o< client_property).
 * A property always belongs to exactly one client — this is why property
 * endpoints are nested under /clients/{clientId}/properties, not standalone.
 * Real-world example: "Hilton Hotel" (one client) might have three
 * properties — their Casablanca location, their Rabat location, and their
 * Tangier location — each a separate address Hassan might be sent to.
 */
@Getter
public class ClientPropertyResponse {

    // Unique identifier for this specific property. Needed by the frontend
    // to reference this exact property later — e.g. when creating a Quote
    // and asking "which property is this job for?"
    private final UUID id;

    // A short, human-friendly nickname for this location, e.g. "Main Office"
    // or "Villa Casablanca". Optional — a client with only one property
    // might not bother naming it. Helps Hassan tell locations apart at a
    // glance in a dropdown, rather than reading full street addresses.
    private final String label;

    // The actual physical address — where the technician needs to go.
    // This is the one truly required field (see ClientPropertyRequest);
    // everything else is optional context around it.
    private final String address;

    private final String city;

    // True if this is the client's default/main location. Useful for
    // pre-selecting a sensible default when creating a new Quote —
    // instead of the frontend showing an empty dropdown, it can
    // pre-fill whichever property has isPrimary = true.
    private final boolean isPrimary;

    // Private constructor + static factory method (same pattern as
    // ApiResponse, UserResponse, CategoryResponse) — this DTO can ONLY be
    // built by converting a real ClientProperty entity via .from(...),
    // never constructed with arbitrary/incomplete data by accident.
    private ClientPropertyResponse(ClientProperty property) {
        this.id = property.getId();
        this.label = property.getLabel();
        this.address = property.getAddress();
        this.city = property.getCity();
        this.isPrimary = property.isPrimary();
    }

    public static ClientPropertyResponse from(ClientProperty property) {
        return new ClientPropertyResponse(property);
    }
}