package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.myutils.annotations.CsvClass;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@CsvClass
public class TestClass {

    private String firstName;

    private String lastName;

    private int dayOfBirth;

    private Months monthOfBirth;

    private Map<Student, Integer> map;

  /*  private Student student;*/

    /*private int yearOfBirth;

    private int[] age;

    private List<Months> student;*/

}
