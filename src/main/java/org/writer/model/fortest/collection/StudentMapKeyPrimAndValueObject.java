package org.writer.model.fortest.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.model.fortest.SimpleObjectWithString;
import org.writer.myutils.annotations.CsvClass;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentMapKeyPrimAndValueObject {

    private String name;

    private Map<Integer, SimpleObjectWithString> map;

}
