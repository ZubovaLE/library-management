package com.zubova.library.common.crud;

import com.zubova.library.common.persistence.BaseEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCrudService<E extends BaseEntity> implements CrudService<E> {

    private final JpaRepository<E, Long> repository;

    @Override
    public List<E> getAll() {
        return repository.findAll();
    }

    @Override
    public E getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity was not found, id = " + id));
    }

    @Override
    public E save(E entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}