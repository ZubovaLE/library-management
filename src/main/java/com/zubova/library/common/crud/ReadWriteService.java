package com.zubova.library.common.crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReadWriteService<E> {

    Page<E> getAll(Pageable pageable);

    E getById(Long id);

    E save(E entity);

}