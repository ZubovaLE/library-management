package com.zubova.library.common.crud;

import com.zubova.library.common.persistence.BaseEntity;
import com.zubova.library.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public abstract class AbstractReadWriteService<E extends BaseEntity, R extends JpaRepository<E, Long>>
        implements ReadWriteService<E> {

    protected final R repository;

    @Override
    public Page<E> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public E getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(getEntityClass().getSimpleName(), id));
    }

    @Override
    public E save(E entity) {
        return repository.save(entity);
    }

    protected abstract Class<E> getEntityClass();

}