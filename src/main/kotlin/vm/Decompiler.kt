package vm

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

}
