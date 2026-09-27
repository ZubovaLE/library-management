package com.zubova.library.common.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudApplicationService<C, D> {

    Page<D> getAll(Pageable pageable);

    D getById(Long id);

    D create(C request);

    D update(Long id, C request);

    void delete(Long id);

}