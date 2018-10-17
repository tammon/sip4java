package net.tammon.sip.packets

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataTest {

    @Test
    fun `expecting to shift by 256 when bigger than 127`() {
        // Arrange
        // Act
        val actual = Data.parseUnsignedByte("128")
        val expected: Byte = -128
        // Assert
        assertEquals(expected, actual)
    }
}