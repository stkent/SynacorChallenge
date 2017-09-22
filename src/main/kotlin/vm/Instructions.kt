package vm

import java.io.PrintWriter

private fun Byte.toUnsignedInt() = java.lang.Byte.toUnsignedInt(this)

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

    intInstructions
            .subList(index + 1, index + opCode.operandCount + 1)
            .map { instruction -> Operand.fromInt(instruction) }
            .forEach { operand -> printOperand(opCode, operand, registers, writer) }
}

private fun printOperand(opCode: OpCode, operand: Operand, registers: IntArray?, writer: PrintWriter) {
    when (operand) {
        is Operand.Register -> {
            printRegisterOperand(operand, registers, writer)
        }

        is Operand.Number -> {
            // Special handling for OUT because operands will often represent ASCII characters.
            if (opCode == OpCode.OUT) {
                printOutNumberOperand(operand, writer)
            } else {
                printIndented("$operand", writer)
            }
        }
    }
}

private fun printOutNumberOperand(number: Operand.Number, writer: PrintWriter) {
        val operandChar = number.value.toChar()

        if (operandChar == '\n') {
            /*
             * Print newline literals (1 line "tall"), rather than inserting actual newlines (2 lines "tall") so
             * that our decompiled file line numbers correspond to instruction indices (offset by 1, since the
             * instruction indices are 0-indexed but the file line numbers are 1-indexed).
             */
            printIndented("\"\\n\"", writer)
        } else {
            printIndented("\"$operandChar\"", writer)
        }
}

private fun printRegisterOperand(register: Operand.Register, registers: IntArray?, writer: PrintWriter) {
    if (registers != null) {
        printIndented("$register = ${registers[register.index]}", writer)
    } else {
        printIndented("$register", writer)
    }
}

private fun printIndented(literal: String, writer: PrintWriter) {
    writer.println("  $literal")
}
