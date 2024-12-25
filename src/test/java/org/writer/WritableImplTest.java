package org.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.writer.myutils.service.WritableImpl;

class WritableImplTest {

    private WritableImpl writableImpl;

    @BeforeEach
    void setUp() {
        writableImpl = new WritableImpl();
    }

    @Test
    @DisplayName("Тест: Генерация токена")
    void testGenerateToken() {
    }

}
