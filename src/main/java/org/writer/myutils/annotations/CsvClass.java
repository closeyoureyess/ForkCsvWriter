package org.writer.myutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     Аннотация, предназначенная для разметки классов, из которых будет формироваться отчет.
 *     Если аннотации нет над классом, который помещается в List и отправляется в метод writeToFile,
 *     будет выброшен exception
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CsvClass {
}
