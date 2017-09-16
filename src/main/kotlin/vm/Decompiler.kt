package vm

import java.io.File

class Decompiler {

    fun decompile(programBytes: ByteArray, outFileName: String) {
        File(outFileName).printWriter().use { writer ->
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
                            writer.println("\"${intOperand.toChar()}\"")
                        } else {
                            writer.println(Operand.fromInt(intOperand))
                        }
                    } else if (operandCount > 0) {
                        intInstructions
                                .subList(index + 1, index + operandCount + 1)
                                .map { instruction -> Operand.fromInt(instruction) }
                                .forEach { operand -> writer.println(operand) }
                    }

                    index += operandCount + 1
                } else {
                    // Represents initial data (vs initial instruction) in binary file.
                    writer.println(intInstruction)
                    index += 1
                }
            }
        }
    }

}
