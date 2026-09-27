package com.zubova.library.common.crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<E> {

    Page<E> getAll(Pageable pageable);

    E getById(Long id);

    E save(E entity);

    void delete(Long id);

}