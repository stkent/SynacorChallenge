package vm

import java.io.PrintWriter

class Decompiler {

    fun decompile(programBytes: ByteArray, writer: PrintWriter) {
        val intInstructions = parseIntInstructions(programBytes)

        var index = 0

        while (index < intInstructions.size) {
            val intInstruction = intInstructions[index]
            val opCode = OpCode.fromInt(intInstruction)

            if (opCode != null) {
                writer.println(opCode)

                val operandCount = opCode.operandCount

                if (opCode == OpCode.OUT) {
                    // Special handling for OUT because operands will often be ASCII characters.
                    val intOperand = intInstructions[index + 1]

                    if (intOperand <= MAX_INT) {
                        val operandChar = intOperand.toChar()

                        if (operandChar == '\n') {
                            /*
                             * Print newline literals (1 line "tall"), rather than inserting actual newlines (2
                             * lines "tall") so that our decompiled file line numbers correspond to instruction
                             * indices (offset by 1, since the instruction indices are 0-indexed but the file line
                             * numbers are 1-indexed).
                             */
                            printOperandValue("\"\\n\"", writer)
                        } else {
                            printOperandValue("\"$operandChar\"", writer)
                        }
                    } else {
                        printOperandValue(Operand.fromInt(intOperand).toString(), writer)
                    }
                } else if (operandCount > 0) {
                    intInstructions
                            .subList(index + 1, index + operandCount + 1)
                            .map { instruction -> Operand.fromInt(instruction) }
                            .forEach { operand -> printOperandValue(operand.toString(), writer) }
                }

                index += operandCount + 1
            } else {
                // Represents a piece of initial data (vs an operation) in the binary file we're decompiling.
                writer.println(intInstruction)
                index += 1
            }
        }
    }

    private fun printOperandValue(literal: String, writer: PrintWriter) {
        writer.println("  $literal")
    }

}
