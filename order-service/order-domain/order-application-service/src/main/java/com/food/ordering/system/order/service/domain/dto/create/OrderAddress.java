package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderAddress(@NotNull UUID id,
                           @NotNull @Max(50) String street,
                           @NotNull @Max(5) String postalCode,
                           @NotNull @Max(50) String city) {
}
