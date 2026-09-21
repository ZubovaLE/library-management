package com.zubova.library.common.application;

import java.util.List;

public interface CrudApplicationService<C, D> {

    List<D> getAll();

    D getById(Long id);

    D create(C request);

    D update(Long id, C request);

    void delete(Long id);

}