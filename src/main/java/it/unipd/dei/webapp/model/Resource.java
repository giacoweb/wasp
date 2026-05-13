package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Marker interface for persistent FinTrack resources.
 */
public interface Resource {

    UUID getId();

    void setId(UUID id);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);
}
