package org.writer.myutils.actions;

import lombok.extern.slf4j.Slf4j;
import org.writer.myutils.annotations.CsvClass;
import org.writer.myutils.annotations.CsvField;
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

    private <T> void checkThatAnnotationPresence(Map<Object, Annotation[]> hashMapWithAnnotations, T typeAnnotation) {
        try {
            hashMapWithAnnotations.entrySet().removeIf(entry -> {
                boolean hasCsvClassAnnotation = Arrays.stream(entry.getValue())
                        .anyMatch(a -> a.annotationType() == typeAnnotation);
                boolean isEnumKey = entry.getKey().getClass().isEnum();

                return hasCsvClassAnnotation && isEnumKey;
            });
            if (hashMapWithAnnotations.isEmpty()) {
                throw new EntitiesForParseNotFoundExceptions(OBJECTS_FOR_REPORT_NOT_FOUND_EXCEPTION.getEnumDescription());
            }
        } catch (EntitiesForParseNotFoundExceptions e) {
            log.error("Возникла ошибка: " + e);
        }
    }

    public boolean test(List<?> list) {
        Map<Object, Field[]> mapWithFieldsObjects = createMapWithObjectAndArrayFields(list);
        for (Map.Entry<Object, Field[]> entry : mapWithFieldsObjects.entrySet()) {
            Object key = entry.getKey();
            Field[] value = entry.getValue();
            Arrays.stream(value).forEach(f -> {
                Class<?> fieldClassType = f.getType();
                if (fieldClassType.isInterface()) {
                   if(f.isAnnotationPresent(CsvField.class)){
                       Annotation annotation = f.getAnnotation(CsvField.class);
                       if(annotation != null && f.getAnnotation(CsvField.class).nestedCollectionClass() != null) {

                       }
                   }
                }
            });
            break;
        }
    }
}
