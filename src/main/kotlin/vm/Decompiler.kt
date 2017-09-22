package vm

import vm.OpCode.OUT
import java.io.PrintWriter

class Decompiler {

    fun decompile(programBytes: ByteArray, writer: PrintWriter) {
        val intInstructions = parseIntInstructions(programBytes)

        var index = 0

        while (index < intInstructions.size) {
            val intInstruction = intInstructions[index]

            @Suppress("LiftReturnOrAssignment")
            if (OpCode.representsOpCode(intInstruction)) {
                printOpCodeAndOperands(
                        index = index,
                        intInstructions = intInstructions,
                        writer = writer)

                index += OpCode.fromInt(intInstruction).operandCount + 1
            } else {
                // Represents a piece of initial data (vs an operation) in the binary file we're decompiling.
                writer.println(intInstruction)

                index += 1
            }
        }
    }

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

        if (opCode == OUT) {
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

}
