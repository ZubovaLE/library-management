package com.zubova.library.common.mapping;

import com.zubova.library.common.persistence.BaseEntity;

import java.util.List;

public interface BaseDtoMapper<E extends BaseEntity, D> {

    D toDto(E entity);

    List<D> toDtos(List<E> entities);

}