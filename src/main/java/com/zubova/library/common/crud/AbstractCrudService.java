package com.zubova.library.common.crud;

import com.zubova.library.common.persistence.BaseEntity;
import com.zubova.library.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCrudService<E extends BaseEntity, R extends JpaRepository<E, Long>>
        implements CrudService<E> {

    protected final R repository;

    @Override
    public List<E> getAll() {
        return repository.findAll();
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

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    protected abstract Class<E> getEntityClass();

}