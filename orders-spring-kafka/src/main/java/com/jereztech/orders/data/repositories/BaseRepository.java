package com.jereztech.orders.data.repositories;

import com.jereztech.orders.data.entities.BaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends ListCrudRepository<T, UUID>, ListPagingAndSortingRepository<T, UUID> {
}
