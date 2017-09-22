package vm

import java.io.PrintWriter

/**
 * Converts an array of bytes representing a program into a list of 16-bit unsigned integer instructions.
 */
fun parseIntInstructions(programBytes: ByteArray): List<Int> {
    val bytesAsUnsignedInts = programBytes.map(Byte::toUnsignedInt)

    val result = mutableListOf<Int>()

    for (i in 0 until bytesAsUnsignedInts.size step 2) {
        val lowByteInt  = bytesAsUnsignedInts[i]
        val highByteInt = bytesAsUnsignedInts[i + 1]
        result.add((highByteInt shl 8) + lowByteInt)
    }

    return result
}

/**
 * NOTE: intInstructions\[index\] MUST represent an OpCode.
 */
fun printOpCodeAndOperands(
        index: Int,
        intInstructions: List<Int>,
        registers: IntArray? = null,
        writer: PrintWriter) {

    val intInstruction = intInstructions[index]
    val opCode = OpCode.fromInt(intInstruction)

    writer.println(opCode)

    val operandCount = opCode.operandCount
    val operands = intInstructions
            .subList(index + 1, index + operandCount + 1)
            .map { instruction -> Operand.fromInt(instruction) }

    if (opCode == OpCode.OUT) {
        // Special handling for OUT because operands will often be ASCII characters.
        printOutOperand(operands[0], registers, writer)
    } else if (operands.isNotEmpty()) {
        operands.forEach { operand -> printOperandValue("$operand", writer) }
    }
}

private fun printOutOperand(operand: Operand, registers: IntArray?, writer: PrintWriter) {
    when (operand) {
        is Operand.Number -> {
            val operandChar = operand.value.toChar()

            if (operandChar == '\n') {
                /*
                 * Print newline literals (1 line "tall"), rather than inserting actual newlines (2 lines "tall") so
                 * that our decompiled file line numbers correspond to instruction indices (offset by 1, since the
                 * instruction indices are 0-indexed but the file line numbers are 1-indexed).
                 */
                printOperandValue("\"\\n\"", writer)
            } else {
                printOperandValue("\"$operandChar\"", writer)
            }
        }

        is Operand.Register -> {
            if (registers != null) {
                printOperandValue("$operand = ${registers[operand.index]}", writer)
            } else {
                printOperandValue("$operand", writer)
            }
        }
    }
}

private fun printOperandValue(literal: String, writer: PrintWriter) {
    writer.println("  $literal")
}
