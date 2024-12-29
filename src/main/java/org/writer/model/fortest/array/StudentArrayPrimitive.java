package org.writer.model.fortest.array;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentArrayPrimitive {

    private String name;

    private int[] arrayPrimitive;

}
