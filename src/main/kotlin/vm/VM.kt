package vm

import Actor
import InteractiveActor
import Printer
import vm.OpCode.*
import vm.Operand.Number
import vm.Operand.Register
import java.util.*

const val MAX_VM_INT = 32767
const val FIRST_REGISTER_INSTRUCTION = MAX_VM_INT + 1
const val REGISTER_COUNT = 8
const val LAST_REGISTER_INSTRUCTION = FIRST_REGISTER_INSTRUCTION + REGISTER_COUNT - 1

/**
 * todo: fill me in
 */
class VM(
    private val actor: Actor = InteractiveActor(),
    private val outputPrinter: Printer,
    registersSeed: (Int) -> Int = { 0 },
    stackSeed: List<Int> = emptyList(),
    private var ip: Int = 0
) {

  private val registers: IntArray = IntArray(REGISTER_COUNT)
  private val stack: Deque<Int> = ArrayDeque<Int>()
  private val memory: MutableList<Int> = mutableListOf()
  private val pendingInputChars: Deque<Char> = ArrayDeque<Char>()

  init {
    (0 until registers.size).forEach { registers[it] = registersSeed(it) }
    stack.addAll(stackSeed)
  }

  fun runProgram(
      programBytes: ByteArray,
      instructionPrinter: Printer? = null) {

    val intInstructions = parseIntInstructions(programBytes)

    memory.addAll(intInstructions)

    var run = true
    val memoryRange = 0 until memory.size

    while (run && ip in memoryRange) {
      if (memory[ip].representsOpCode()) {
        val opCode = OpCode.fromInt(memory[ip])

        val operands = intInstructions
            .subList(ip + 1, ip + opCode.operandCount + 1)
            .map { int -> Operand.fromInt(int) }

        instructionPrinter?.let {
          val instructionDisplayString = instructionDisplayString(
              opCode = opCode,
              operands = operands,
              registers = registers)

          it.printLine(instructionDisplayString)
        }

        run = processOpCode(opCode, operands)
      } else {
        break
      }
    }
  }

  fun getRegisterValues() = registers.toList()

  // Implementation

  private fun processOpCode(
      opCode: OpCode,
      operands: List<Operand>): Boolean {

    require(opCode.operandCount == operands.size) { "Incorrect number of operands supplied." }

    operands.forEach { operand ->
      if (operand is Register && operand.index == 7) {
        outputPrinter.printLine("Hit register 7 during instruction $ip")
      }
    }

    when (opCode) {
      HALT -> {
        ip += 1

        return false
      }

      SET -> {
        val target = operands[0] as? Register ?: return false
        val operand = operands[1]

        registers[target.index] = operandToInt(operand)
        ip += 3

        return true
      }

      PUSH -> {
        val operand = operands[0]

        stack.addFirst(operandToInt(operand))
        ip += 2

        return true
      }

      POP -> {
        val value = stack.pollFirst() ?: return false
        val target = operands[0] as? Register ?: return false

        registers[target.index] = value
        ip += 2

        return true
      }

      EQ -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        val equal = operandToInt(operand2) == operandToInt(operand3)

        registers[target.index] = if (equal) 1 else 0
        ip += 4

        return true
      }

      GT -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        val greaterThan = operandToInt(operand2) > operandToInt(operand3)

        registers[target.index] = if (greaterThan) 1 else 0
        ip += 4

        return true
      }

      JMP -> {
        val operand = operands[0]

        ip = operandToInt(operand)

        return true
      }

      JT -> {
        val operand1 = operands[0]
        val operand2 = operands[1]

        val jump = operandToInt(operand1) != 0

        ip = if (jump) operandToInt(operand2) else ip + 3

        return true
      }

      JF -> {
        val operand1 = operands[0]
        val operand2 = operands[1]

        val jump = operandToInt(operand1) == 0

        ip = if (jump) operandToInt(operand2) else ip + 3

        return true
      }

      ADD -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        registers[target.index] = (operandToInt(operand2) + operandToInt(operand3)) % 32768
        ip += 4

        return true
      }

      MULT -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        registers[target.index] = (operandToInt(operand2) * operandToInt(operand3)) % 32768
        ip += 4

        return true
      }

      MOD -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        registers[target.index] = operandToInt(operand2).rem(operandToInt(operand3))
        ip += 4

        return true
      }

      AND -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        registers[target.index] = operandToInt(operand2) and operandToInt(operand3)
        ip += 4

        return true
      }

      OR -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]
        val operand3 = operands[2]

        registers[target.index] = operandToInt(operand2) or operandToInt(operand3)
        ip += 4

        return true
      }

      NOT -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]

        registers[target.index] = operandToInt(operand2).inv() and ((1 shl 15) - 1)
        ip += 3

        return true
      }

      RMEM -> {
        val target = operands[0] as? Register ?: return false
        val operand2 = operands[1]

        registers[target.index] = memory[operandToInt(operand2)]
        ip += 3

        return true
      }

      WMEM -> {
        val operand1 = operands[0]
        val operand2 = operands[1]

        memory[operandToInt(operand1)] = operandToInt(operand2)
        ip += 3

        return true
      }

      CALL -> {
        val operand = operands[0]

        stack.addFirst(ip + 2)
        ip = operandToInt(operand)

        return true
      }

      RET -> {
        val value = stack.pollFirst() ?: return false

        ip = value

        return true
      }

      OUT -> {
        val operand = operands[0]
        val char = operandToInt(operand).toChar()

        outputPrinter.printChar(char)
        ip += 2

        return true
      }

      IN -> {
        val target = operands[0] as? Register ?: return false

        if (pendingInputChars.isEmpty()) {
          // Fetch new input.
          val inputLine = actor.getInputLine()

          if (processSpecialInstruction(inputLine)) {
            /*
             * By returning true without queuing up the input characters for processing or
             * incrementing the ip, we force another request for input in the very next run loop.
             */
            return true
          }

          inputLine.forEach { char -> pendingInputChars.add(char) }
          pendingInputChars.add('\n')
        } else {
          // Process pending input.
          registers[target.index] = pendingInputChars.poll().toInt()
          ip += 2
        }

        return true
      }

      NOOP -> {
        ip += 1

        return true
      }
    }
  }

  private fun operandToInt(operand: Operand) = when (operand) {
    is Number -> operand.value
    is Register -> registers[operand.index]
  }

  // Special instruction processing

  private class SpecialInstruction(
      val regex: Regex,
      val action: (matchResult: MatchResult, vm: VM) -> Unit)

  private val specialInstructions = arrayOf(
      // Prints the current instruction pointer value.
      SpecialInstruction(
          regex = "vm-print-ip".toRegex(),
          action = { _, vm ->
            vm.outputPrinter.printLine("ip = ${vm.ip}")
          }
      ),
      // Prints current register values.
      SpecialInstruction(
          regex = "vm-print-register-values".toRegex(),
          action = { _, vm ->
            vm.outputPrinter.printLine("register values = ${vm.getRegisterValues()}")
          }
      ),
      // Prints current register values.
      SpecialInstruction(
          regex = "vm-print-stack".toRegex(),
          action = { _, vm ->
            vm.outputPrinter.printLine("stack = ${vm.stack}")
          }
      ),
      // Sets register 7 to the provided value.
      SpecialInstruction(
          regex = "vm-set-register-7 (\\d+)".toRegex(),
          action = { matchResult, vm ->
            vm.registers[7] = matchResult.groups[1]!!.value.toInt()
          }
      )
  )

  /**
   * Checks the provided [inputLine] against a collection of pre-defined special instructions. If a
   * special instruction is matched, it is executed and this method returns `true`. If no special
   * instruction is matched, this method returns `false`.
   */
  private fun processSpecialInstruction(inputLine: String): Boolean {
    specialInstructions.forEach { specialInstruction ->
      specialInstruction.regex.matchEntire(inputLine)?.let { matchResult ->
        specialInstruction.action(matchResult, this@VM)
        return true
      }
    }

    return false
  }

}
