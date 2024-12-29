package org.writer.model.fortest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SimpleObjectWithArrayObject {

    private String name;

    private int[] arraySimpleObjectWithStrings;

}
