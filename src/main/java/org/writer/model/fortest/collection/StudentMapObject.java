package org.writer.model.fortest.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.model.fortest.SimpleObjectPrimitArrayCollect;
import org.writer.myutils.annotations.CsvClass;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentMapObject {

    private String name;

    private Map<SimpleObjectPrimitArrayCollect, SimpleObjectPrimitArrayCollect> map;

}
