package org.writer.model.fortest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentNestedObject {

    private String name;

    private SimpleObjectWithString simpleObjectWithString;

}
