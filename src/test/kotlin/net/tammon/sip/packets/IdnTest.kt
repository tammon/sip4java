package net.tammon.sip.packets

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class IdnTest {

    @Test
    fun `should be able to deal with bytes larger than 127`() {
        // act
        val actual = Idn.getIdnAsByteArray("S-0-0001.255.128")
        val expected = byteArrayOf(1,0,-128,-1)
        // assert
        assertArrayEquals(expected, actual)
    }

    @Test
    fun `should return right idn for multi digit numbers`() {
        // act
        val actual = Idn.getIdnAsByteArray("S-0-0001.12.1")
        val expected = byteArrayOf(1,0,1,12)
        // assert
        assertArrayEquals(expected, actual)
    }

    @Test
    fun `should return right idn for P parameters`() {
        // act
        val actual = Idn.getIdnAsByteArray("P-0-0001.0.0")
        val expected = byteArrayOf(1,-128,0,0)
        // assert
        assertArrayEquals(expected, actual)
    }
}