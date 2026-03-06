package com.kousenit.http;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled("Open Notify API is no longer reliably maintained")
class ISSPositionTest {

    @Test
    @DisplayName("ISSPosition demo executes without errors")
    void testISSPositionDemo() {
        assertDoesNotThrow(() -> ISSPosition.main(new String[]{}));
    }
}
