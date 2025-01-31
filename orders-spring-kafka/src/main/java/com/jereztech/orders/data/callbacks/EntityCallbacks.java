package com.jereztech.orders.data.callbacks;

import com.jereztech.orders.data.entities.BaseEntity;
import org.springframework.core.annotation.Order;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Order(HIGHEST_PRECEDENCE)
@Component
public class EntityCallbacks implements BeforeConvertCallback<BaseEntity> {

    @Override
    public BaseEntity onBeforeConvert(BaseEntity aggregate) {
        // auditing data here
        if (aggregate.isNew()) {
            aggregate.setId(UUID.randomUUID());
        }
        return aggregate;
    }

}
