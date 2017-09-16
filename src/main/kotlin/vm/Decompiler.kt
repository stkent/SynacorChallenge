package vm

class Decompiler {

    fun decompile(programBytes: ByteArray) {
        val intInstructions = parseIntInstructions(programBytes)

        var index = 0

        while (index < intInstructions.size) {
            val intInstruction = intInstructions[index]
            val opCode = OpCode.fromInt(intInstruction)

            if (opCode != null) {
                println(opCode)

                val operandCount = opCode.operandCount

                if (operandCount > 0) {
                    printOperands(intInstructions.subList(index + 1, index + operandCount + 1))
                }

                index += operandCount + 1
            } else {
                println(intInstruction)
                index += 1
            }
        }
    }

    private fun printOperands(intInstructions: List<Int>) {
        intInstructions
                .map { instruction -> Operand.fromInt(instruction) }
                .forEach { operand -> println(operand) }
    }

}
