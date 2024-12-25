package org.writer.myutils.actions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public interface DataActions {

    Map<Object, Field[]> createMapWithObjectAndArrayFields(List<?> objectsForCsv);

    <T> void filterAnnotationInputList(List<?> data, T typeAnnotation);

}
