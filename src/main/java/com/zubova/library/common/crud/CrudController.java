package com.zubova.library.common.crud;

import com.zubova.library.common.application.CrudApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
public abstract class CrudController<C, D> {

    private final CrudApplicationService<C, D> crudApplicationService;

    @GetMapping
    public List<D> getAll() {
        return crudApplicationService.getAll();
    }

    @GetMapping("/{id}")
    public D getById(@PathVariable Long id) {
        return crudApplicationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public D create(@Valid @RequestBody C request) {
        return crudApplicationService.create(request);
    }

    @PutMapping("/{id}")
    public D update(@PathVariable Long id, @Valid @RequestBody C request) {
        return crudApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        crudApplicationService.delete(id);
    }

}