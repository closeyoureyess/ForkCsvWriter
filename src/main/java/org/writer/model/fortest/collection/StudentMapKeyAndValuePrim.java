package org.writer.model.fortest.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentMapKeyAndValuePrim {

    private String name;

    private Map<Integer, Integer> map;

}
