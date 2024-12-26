package org.writer.myutils.actions;

import lombok.extern.slf4j.Slf4j;
import org.writer.myutils.other.exceptions.EntitiesForParseNotFoundExceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.writer.myutils.other.exceptions.DescriptionUserExeption.OBJECTS_FOR_REPORT_NOT_FOUND_EXCEPTION;

@Slf4j
public class DataActionsImpl implements DataActions {

    @Override
    public Map<Object, Field[]> createMapWithObjectAndArrayFields(List<?> objectsForCsv) {
        Map<Object, Field[]> mapWithFieldsObjects = new HashMap<>();
        objectsForCsv.forEach(o -> {
            Field[] fields = o.getClass().getDeclaredFields();
            mapWithFieldsObjects.put(o, fields);
        });
        return mapWithFieldsObjects;
    }

    @Override
    public <T> void filterAnnotationInputList(List<?> data, T typeAnnotation) {
        Annotation[] annotations = null;
        Map<Object, Annotation[]> hashMapWithAnnotations;
        Object objectFromList = null;
        if (data != null && !data.isEmpty()) {
            objectFromList = data.getFirst();
            annotations = objectFromList.getClass().getAnnotations();
        }
        if (annotations != null && annotations.length != 0) {
            hashMapWithAnnotations = new HashMap<>();
            hashMapWithAnnotations.put(objectFromList, annotations);
            checkThatAnnotationPresence(hashMapWithAnnotations, typeAnnotation);
        }
    }

    /**
     * Проверяет наличие переданной аннотации в объекте
     *
     * @param hashMapWithAnnotations Map с объектом-примером и аннотациями этого объекта
     * @param typeAnnotation Аннотация, наличие которой пытаемся проверить
     * @param <T> Тип аннотации
     */
    private <T> void checkThatAnnotationPresence(Map<Object, Annotation[]> hashMapWithAnnotations, T typeAnnotation) {
        try {
            hashMapWithAnnotations.entrySet().removeIf(entry -> {
                boolean hasCsvClassAnnotation = Arrays.stream(entry.getValue())
                        .anyMatch(a -> a.annotationType() == typeAnnotation);
                boolean isEnumKey = entry.getKey().getClass().isEnum();
                boolean isPrimitiveKey = entry.getKey().getClass().isPrimitive();
                boolean isArrayKey = entry.getKey().getClass().isArray();

                return hasCsvClassAnnotation && (isEnumKey || isPrimitiveKey|| isArrayKey);
            });
            if (hashMapWithAnnotations.isEmpty()) {
                throw new EntitiesForParseNotFoundExceptions(OBJECTS_FOR_REPORT_NOT_FOUND_EXCEPTION.getEnumDescription());
            }
        } catch (EntitiesForParseNotFoundExceptions e) {
            log.error("Возникла ошибка: " + e);
        }
    }
}
