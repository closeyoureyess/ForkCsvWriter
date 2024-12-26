package org.writer.myutils.actions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public interface DataActions {

    /**
     * Метод, позволяющий создать Map с объектом в качестве ключа и массивом полей, которые есть в объекте, в качестве значения
     *
     * @param objectsForCsv Список объектов для формирования отчета
     * @return Map с объектами в качестве ключа и массивом полей этого объекта в значении
     */
    Map<Object, Field[]> createMapWithObjectAndArrayFields(List<?> objectsForCsv);

    /**
     * Метод, позволяющий у одного объекта-примера проверить наличие аннотации, которой объект помечается как пригодный для формирования отчета
     *
     * @param data Список объектов для формирования отчета
     * @param typeAnnotation  Аннотация, наличие которой пытаемся проверить
     * @param <T> Тип аннотации
     */
    <T> void filterAnnotationInputList(List<?> data, T typeAnnotation);

}
