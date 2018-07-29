package vm

private fun Byte.toUnsignedInt() = java.lang.Byte.toUnsignedInt(this)

/**
 * Converts an array of bytes representing a program into a list of 16-bit unsigned integer
 * instructions.
 */
fun parseIntInstructions(programBytes: ByteArray): List<Int> {
  val bytesAsUnsignedInts = programBytes.map(Byte::toUnsignedInt)

  val result = mutableListOf<Int>()

  for (i in 0 until bytesAsUnsignedInts.size step 2) {
    val lowByteInt = bytesAsUnsignedInts[i]
    val highByteInt = bytesAsUnsignedInts[i + 1]
    result.add((highByteInt shl 8) + lowByteInt)
  }

  return result
}

// Display string methods:

fun instructionDisplayString(
    opCode: OpCode,
    operands: List<Operand>,
    registers: IntArray? = null): String {

  require(opCode.operandCount == operands.size) { "Incorrect number of operands supplied." }

  var result: String = opCode.toString()

  operands.forEach {
    result += "\n${operandDisplayString(it, opCode, registers)}"
  }

  return result
}

private fun operandDisplayString(
    operand: Operand,
    opCode: OpCode,
    registers: IntArray?): String {

  return when (operand) {
    is Operand.Register -> {
      registerOperandDisplayString(operand, registers)
    }

    is Operand.Number -> {
      // Special handling because OUT operands are often ASCII characters.
      if (opCode == OpCode.OUT) {
        numberOperandDisplayString(operand)
      } else {
        indentedString("$operand")
      }
    }
  }
}

private fun registerOperandDisplayString(
    register: Operand.Register,
    registers: IntArray?): String {

  return if (registers != null) {
    indentedString("$register = ${registers[register.index]}")
  } else {
    indentedString("$register")
  }
}

private fun numberOperandDisplayString(number: Operand.Number): String {
  val operandChar = number.value.toChar()

  return if (operandChar == '\n') {
    /*
     * Print newline literals (1 line "tall"), rather than inserting actual newlines (2 lines
     * "tall") so that our decompiled file line numbers correspond to instruction indices (offset
     * by 1, since the instruction indices are 0-indexed but the file line numbers are 1-indexed).
     */
    indentedString("\"\\n\"")
  } else {
    indentedString("\"$operandChar\"")
  }
}

private fun indentedString(literal: String): String {
  return "  $literal"
}
