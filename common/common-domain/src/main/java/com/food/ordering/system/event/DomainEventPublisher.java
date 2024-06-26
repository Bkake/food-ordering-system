package com.food.ordering.system.event;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
