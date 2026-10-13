package com.zubova.library.reader;

import com.zubova.library.common.crud.CrudController;
import com.zubova.library.reader.dto.ReaderDto;
import com.zubova.library.reader.dto.ReaderRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@Tag(
        name = "Readers",
        description = "Reader management"
)
public class ReaderCrudController extends CrudController<ReaderRequestDto, ReaderDto> {

    public ReaderCrudController(ReaderService readerService) {
        super(readerService);
    }

}