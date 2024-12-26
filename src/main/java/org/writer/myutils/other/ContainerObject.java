package org.writer.myutils.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContainerObject {

    private String valueOne;

    private String valueTwo;

}
