package org.writer.model.fortest.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.model.fortest.SimpleObjectWithString;
import org.writer.myutils.annotations.CsvClass;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class StudentSetObject {

    private String name;

    private Set<SimpleObjectWithString> score;

}
