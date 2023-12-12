package com.github.erotourtes.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MathParserTest {
    @Test
    @DisplayName("Double input")
    fun doubleInput() {
//        val parser = MathParser("1.0+2.0")
        val parser = MathParser("1.0-2.0")
        assertEquals("1.02.0+", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test simple binary operators [+, -]")
    fun simple1() {
        val parser = MathParser("1 + 2 - 3")
        assertEquals("12+3-", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test simple binary operators [+, -, *, /]")
    fun simple2() {
        val parser = MathParser("1 + 2 - 3 * 4 / 5")
        assertEquals("12+34*5/-", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test simple binary operators [+, -, *, /, ^]")
    fun simple3() {
        val parser = MathParser("1 + 2 - 3 * 4 / 5 ^ 6 - 3")
        assertEquals("12+34*56^/-3-", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test simple binary operators [+, -, *, /, ^] with brackets")
    fun simple4() {
        val parser = MathParser("(1 + 2 - 3) * 4 / 5 ^ 6 - 3")
        assertEquals("12+3-4*56^/3-", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test with functions")
    fun fn1() {
        val parser = MathParser("sin(30) * 4 + 3")
        assertEquals("30sin4*3+", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test all operators")
    fun fn2() {
        val parser = MathParser("sin(30) * 4 + cos(31) - (3 - 2) / 3 ^ (4 + 5)")
        assertEquals("30sin4*31cos+32-345+^/-", parser.rpn.joinToString(""))
    }

    @Test
    @DisplayName("Test with variables")
    fun variables1() {
        val parser = MathParser("x + 2")
        parser.setVariable("x", 3.0)
        assertEquals("x2+", parser.rpn.joinToString(""))
        assertEquals(5.0, parser.eval())
    }
}
