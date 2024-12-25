package org.writer.myutils.other.exceptions;

/**
 * <pre>
 *     Enum с описанием для кастомных ошибок, которые могут выбрасываться в некоторых частях приложения
 * </pre>
 */
public enum DescriptionUserExeption {

    OBJECTS_FOR_REPORT_NOT_FOUND_EXCEPTION("В переданной коллекции объектов для генерации отчётов не найдено. Разметьте объекты аннотацией @CsvClass библиотеки и попробуйте еще раз");

    private String enumDescription;

    DescriptionUserExeption(String enumDescription) {
        this.enumDescription = enumDescription;
    }

    public String getEnumDescription() {
        return enumDescription;
    }
}
