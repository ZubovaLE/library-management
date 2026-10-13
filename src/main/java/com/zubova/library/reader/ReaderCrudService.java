package com.zubova.library.reader;

import com.zubova.library.common.crud.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class ReaderCrudService extends AbstractCrudService<Reader, ReaderRepository> {

    public ReaderCrudService(ReaderRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Reader> getEntityClass() {
        return Reader.class;
    }

}