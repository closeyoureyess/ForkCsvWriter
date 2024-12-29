package org.writer.model.fortest.array;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.model.fortest.SimpleObjectWithArrayObject;
import org.writer.myutils.annotations.CsvClass;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentArrayObjectWithNestedArrayObject {

    private String name;

    private SimpleObjectWithArrayObject[] arraySimpleObjectWithStrings;

}
