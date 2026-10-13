package com.zubova.library.common.crud;

public interface CrudService<E> extends ReadWriteService<E> {

    void delete(Long id);

}