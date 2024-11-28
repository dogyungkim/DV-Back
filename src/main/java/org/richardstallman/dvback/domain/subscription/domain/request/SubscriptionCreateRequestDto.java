package org.richardstallman.dvback.domain.subscription.domain.request;

import jakarta.validation.constraints.NotNull;

public record SubscriptionCreateRequestDto(@NotNull Long userId, @NotNull Long jobId) {}
