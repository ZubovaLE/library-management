package com.zubova.library.common.application;

import com.zubova.library.common.crud.CrudService;
import com.zubova.library.common.mapping.BaseDtoMapper;
import com.zubova.library.common.persistence.BaseEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCrudApplicationService<E extends BaseEntity, C, D>
        implements CrudApplicationService<C, D> {

    private final CrudService<E> crudService;
    private final BaseDtoMapper<E, D> dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<D> getAll() {
        return dtoMapper.toDtos(crudService.getAll());
    }

    @Override
    @Transactional(readOnly = true)
    public D getById(Long id) {
        return dtoMapper.toDto(crudService.getById(id));
    }

    @Override
    @Transactional
    public D create(C request) {
        E entity = createEntity(request);
        E savedEntity = crudService.save(entity);
        return dtoMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public D update(Long id, C request) {
        E entity = crudService.getById(id);

        updateEntity(request, entity);

        return dtoMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        crudService.delete(id);
    }

    protected abstract E createEntity(C request);

    protected abstract void updateEntity(C request, E entity);

}