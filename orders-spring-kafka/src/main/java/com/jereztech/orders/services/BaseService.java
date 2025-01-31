package com.jereztech.orders.services;

import com.jereztech.orders.data.entities.BaseEntity;
import com.jereztech.orders.data.repositories.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public abstract class BaseService<T extends BaseEntity, R extends BaseRepository<T>> implements IBaseService<T> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public Collection<T> saveAll(Collection<T> entities) {
        return repository.saveAll(entities);
    }

    public void delete(T entity) {
        repository.delete(entity);
    }

    public void deleteAll(Collection<T> entities) {
        repository.deleteAll(entities);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public T getById(UUID id) {
        return repository.findById(id).orElseThrow();
    }

    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

}
