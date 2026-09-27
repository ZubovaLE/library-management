package com.zubova.library.common.mapping;

import com.zubova.library.common.persistence.BaseEntity;

public interface BaseDtoMapper<E extends BaseEntity, D> {

    D toDto(E entity);

}