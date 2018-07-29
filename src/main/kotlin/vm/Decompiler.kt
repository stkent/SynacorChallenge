package vm

class Decompiler {

  fun decompile(programBytes: ByteArray, outputHandler: OutputHandler) {
    val intInstructions = parseIntInstructions(programBytes)

    var index = 0

    while (index < intInstructions.size) {
      val intInstruction = intInstructions[index]

      if (intInstruction.representsOpCode()) {
        val opCode = OpCode.fromInt(intInstruction)

        val operands = intInstructions
            .subList(index + 1, index + opCode.operandCount + 1)
            .map { int -> Operand.fromInt(int) }

        val instructionString = instructionDisplayString(opCode = opCode, operands = operands)
        outputHandler.handleOutput(instructionString)

        index += OpCode.fromInt(intInstruction).operandCount + 1
      } else {
        // Represents a piece of initial data (vs an operation) in the binary file we're decompiling.
        outputHandler.handleOutput(intInstruction.toString())

        index += 1
      }
    }
  }

}
