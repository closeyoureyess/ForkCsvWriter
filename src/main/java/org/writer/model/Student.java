package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class Student {

    private String name;

    private List<String> score;
}