package org.writer.model.fortest.array;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.model.fortest.SimpleObjectWithString;
import org.writer.myutils.annotations.CsvClass;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentArrayObjects {

    private String name;

    private SimpleObjectWithString[] arraySimpleObjectWithStrings;

}
