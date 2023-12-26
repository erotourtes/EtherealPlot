package com.github.erotourtes.utils

import java.lang.IllegalStateException
import kotlin.math.*

private val binaryOperators = mapOf(
    "+" to Pair(1) { a: Double, b: Double -> a + b },
    "-" to Pair(1) { a: Double, b: Double -> a - b },
    "*" to Pair(2) { a: Double, b: Double -> a * b },
    "/" to Pair(2) { a: Double, b: Double -> a / b },
    "^" to Pair(3) { a: Double, b: Double -> a.pow(b) },
)

private val fnOperators = mapOf(
    "sin" to Pair(4) { a: Double -> sin(a) },
    "cos" to Pair(4) { a: Double -> cos(a) },
    "tan" to Pair(4) { a: Double -> tan(a) },
    "ln" to Pair(4) { a: Double -> ln(a) },
    "sqrt" to Pair(4) { a: Double -> sqrt(a) },
)

private val constants = mapOf(
    "pi" to PI,
    "e" to E,
)

private val brackets = listOf("(", ")")

class MathParser(expression: String) {
    var boundaries: Pair<Double, Double> = Pair(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        private set

    val rpn = toRpnOrEmpty(expression)

    private val variables = mutableMapOf("x" to 0.0)

    fun setVariable(name: String, value: Double): MathParser {
        variables[name] = value
        return this
    }

    inline fun evalOrNull(block: (Double) -> Double = { it }): Double? {
        return try {
            block(eval())
        } catch (e: IllegalStateException) {
            null
        }
    }

    fun eval(): Double {
        if (rpn.isEmpty()) throw IllegalStateException("Invalid expression")
        try {
            return _eval()
        } catch (e: Exception) {
            throw IllegalStateException("Invalid expression")
        }
    }

    @Throws(NoSuchElementException::class)
    private fun _eval(): Double {
        val stack = mutableListOf<Double>()
        for (token in rpn) {
            when (token) {
                in binaryOperators -> {
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    stack.add(binaryOperators[token]!!.second(a, b))
                }

                in fnOperators -> {
                    val a = stack.removeLast()
                    stack.add(fnOperators[token]!!.second(a))
                }

                in variables -> stack.add(variables[token]!!)
                else -> stack.add(token.toDouble())
            }
        }
        return stack.removeLast()
    }

    private fun toRpnOrEmpty(exp: String): List<String> = runCatching {
        toRPN(exp)
    }.getOrDefault(emptyList())

    /**
     * Reverse Polish Notation
     * https://en.wikipedia.org/wiki/Reverse_Polish_notation
     * */
    @Throws(NoSuchElementException::class)
    private fun toRPN(exp: String): List<String> {
        val tokens = tokenizeWithBoundaries(exp)

        val operationStack = mutableListOf<String>()
        val queue = mutableListOf<String>()

        for (token in tokens) {
            when (token) {
                in binaryOperators -> handleBinaryOp(operationStack, token, queue)
                in fnOperators -> operationStack.add(token)
                "(" -> operationStack.add(token)
                ")" -> handleClosingBracket(operationStack, queue)
                else -> queue.add(token)
            }
        }

        while (operationStack.isNotEmpty()) queue.add(operationStack.removeLast())

        return queue
    }

    private fun handleClosingBracket(
        operationStack: MutableList<String>,
        queue: MutableList<String>
    ) {
        while (operationStack.last() != "(") queue.add(operationStack.removeLast())
        operationStack.removeLast() // remove "("

        if (operationStack.lastOrNull() in fnOperators)
            queue.add(operationStack.removeLast())
    }

    private fun handleBinaryOp(
        operationStack: MutableList<String>,
        token: String,
        queue: MutableList<String>
    ) {
        while (operationStack.lastOrNull() in binaryOperators) {
            val lastPrecedence = binaryOperators[operationStack.last()]!!.first
            val curPrecedence = binaryOperators[token]!!.first
            if (curPrecedence > lastPrecedence) break

            queue.add(operationStack.removeLast())
        }
        operationStack.add(token)
    }

    // TODO: use string builder
    private fun tokenizeWithBoundaries(expression: String): List<String> {
        if (!expression.contains(",")) return tokenize(expression)

        val (expressionPart, boundariesPart) = expression.split(",")
        val (leftBoundary, rightBoundary) = boundariesPart
            .replace("[", "")
            .replace("]", "")
            .split(";")
            .map { it.trim().toDouble() }

        boundaries = Pair(leftBoundary, rightBoundary)

        return tokenize(expressionPart)
    }

    // TODO: use string builder
    private fun tokenize(expression: String): List<String> {
        var exp = expression
        // 3+log(2, 3) -> 3 + log (2 , 3)
        for (operator in binaryOperators.keys) exp = exp.replace(operator, " $operator ")

        // sin(2) -> sin ( 2 )
        for (bracket in brackets) exp = exp.replace(bracket, " $bracket ")

        // 3x -> 3 * x
        exp = exp.replace(Regex("(\\d+)([a-zA-Z]+)"), "$1 * $2")

        // constants
        for (constant in constants.keys) exp = exp.replace(constant, constants[constant].toString())

        return exp.split("\\s+".toRegex()).filter { it.isNotBlank() }
    }
}
