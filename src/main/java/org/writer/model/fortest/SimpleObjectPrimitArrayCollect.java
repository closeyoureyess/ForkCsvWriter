package org.writer.model.fortest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class SimpleObjectPrimitArrayCollect {

    private String name;

    private int[] age;

    private List<String> list;

}
