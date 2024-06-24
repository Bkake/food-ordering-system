package com.food.ordering.system.order.service.domain.dto.create;

import com.food.ordering.system.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record CreateOrderResponse(@NotNull UUID orderTracking, @NotNull OrderStatus orderStatus,
                                  @NotNull String message) {
}
