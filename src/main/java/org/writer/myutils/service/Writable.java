package org.writer.myutils.service;

import java.util.List;

public interface Writable {

    void writeToFile(List<?> data, String fileName);

}
