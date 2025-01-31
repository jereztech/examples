package com.jereztech.orders.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

}
