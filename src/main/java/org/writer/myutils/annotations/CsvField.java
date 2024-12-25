package org.writer.myutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     Аннотация, предназначенная для разметки коллекций, являющихся полями в классах для отчетов и наполненных пользовательскими класами
 *     </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvField {

    /**
     * Полное название(с пакетом) пользовательского класса которым наполнена коллекция. Если коллекция - Map, можно указать два названия.
     */
    String[] nestedCollectionClass();

}
