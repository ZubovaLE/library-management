package com.zubova.library.common.crud;

import java.util.List;

public interface CrudService<E> {

    List<E> getAll();

    E getById(Long id);

    E save(E entity);

    void delete(Long id);

}