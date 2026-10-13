package com.zubova.library.common.crud;

import com.zubova.library.common.persistence.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractCrudService<E extends BaseEntity, R extends JpaRepository<E, Long>>
        extends AbstractReadWriteService<E, R> implements CrudService<E> {

    public AbstractCrudService(R repository) {
        super(repository);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}