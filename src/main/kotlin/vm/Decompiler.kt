package vm

import java.io.PrintWriter

class Decompiler {

    fun decompile(programBytes: ByteArray, writer: PrintWriter) {
        val intInstructions = parseIntInstructions(programBytes)

        var index = 0

        while (index < intInstructions.size) {
            val intInstruction = intInstructions[index]

            @Suppress("LiftReturnOrAssignment")
            if (intInstruction.representsOpCode()) {
                val opCode = OpCode.fromInt(intInstruction)
                val operands = intInstructions
                        .subList(index + 1, index + opCode.operandCount + 1)
                        .map { int -> Operand.fromInt(int) }

                printOpCodeAndOperands(opCode = opCode, operands = operands, writer = writer)

                index += OpCode.fromInt(intInstruction).operandCount + 1
            } else {
                // Represents a piece of initial data (vs an operation) in the binary file we're decompiling.
                writer.println(intInstruction)

                index += 1
            }
        }
    }

}
