package vm

/**
 * Converts an array of bytes representing a program into a list of 16-bit unsigned integer
 * instructions.
 */
fun parseIntInstructions(programBytes: ByteArray): List<Int> {
  return programBytes
      .map(Byte::toUnsignedInt)
      .toPairs()
      .map { (it.second shl 8) + it.first } // Little-endian interpretation of paired bytes.
}

private fun Byte.toUnsignedInt() = java.lang.Byte.toUnsignedInt(this)

fun <T> Iterable<T>.toPairs(): List<Pair<T, T>> {
  require(count() % 2 == 0) { "Iterable can only be converted to pairs when it has even length." }

  return this
      .windowed(size = 2, step = 2, partialWindows = false)
      .map { Pair(it.first(), it.last()) }
}

// Display string methods

fun instructionDisplayString(
    opCode: OpCode,
    operands: List<Operand>,
    registers: IntArray? = null): String {

  require(opCode.operandCount == operands.size) { "Incorrect number of operands supplied." }

  var result: String = opCode.toString()

  operands.forEach {
    result += "\n  ${operandDisplayString(it, opCode, registers)}"
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
        charOperandDisplayString(operand)
      } else {
        "$operand"
      }
    }
  }
}

private fun registerOperandDisplayString(
    register: Operand.Register,
    registers: IntArray?): String {

  return if (registers != null) {
    "$register = ${registers[register.index]}"
  } else {
    "$register"
  }
}

fun charOperandDisplayString(number: Operand.Number): String {
  val operandChar = number.value.toChar()

  return if (operandChar == '\n') {
    /*
     * Print newline literals (1 line "tall"), rather than inserting actual newlines (2 lines
     * "tall") so that our decompiled file line numbers correspond to instruction indices (offset
     * by 1, since the instruction indices are 0-indexed but the file line numbers are 1-indexed).
     */
    "\"\\n\""
  } else {
    "\"$operandChar\""
  }
}
