package org.writer.myutils.service;

import java.util.List;

public interface Writable {

    /**
     * Метод, позволяющий записать данные в файл-отчет
     *
     * @param data List с объектами, из которых необходимо сделать отчет
     * @param fileName Наименование файла, с которым необходимо сделать отчет
     */
    void writeToFile(List<?> data, String fileName);

}
