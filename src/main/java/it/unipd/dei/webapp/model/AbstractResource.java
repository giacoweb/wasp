package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Shared base class for persistent model entities.
 */
public abstract class AbstractResource implements Resource {

    private UUID id;
    private LocalDateTime createdAt;

    protected AbstractResource() {
    }

    protected AbstractResource(final UUID id, final LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(final UUID id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
