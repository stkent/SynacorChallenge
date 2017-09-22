package vm

import Actor
import InteractiveActor
import vm.OpCode.*
import vm.Operand.Number
import vm.Operand.Register
import java.util.*

const val MAX_INT = 32767
const val FIRST_REGISTER_INSTRUCTION = MAX_INT + 1
const val REGISTER_COUNT = 8
const val LAST_REGISTER_INSTRUCTION = FIRST_REGISTER_INSTRUCTION + REGISTER_COUNT - 1

private val REGISTER_7_INSTRUCTION_NUMBERS = arrayOf(521, 5451, 5522, 6042)

/**
 * todo: fill me in
 */
class VM(
        private val actor: Actor        = InteractiveActor,
        registersSeed:     (Int) -> Int = { 0 },
        stackSeed:         List<Int>    = emptyList(),
        private var ip:    Int          = 0
) {

    private val registers: IntArray         = IntArray(REGISTER_COUNT)
    private val stack:     Deque<Int>       = ArrayDeque<Int>()
    private val memory:    MutableList<Int> = mutableListOf()
    private val lastInput: Deque<Char>      = ArrayDeque<Char>()

    init {
        (0 until registers.size).forEach { registers[it] = registersSeed(it) }
        stack.addAll(stackSeed)
    }

    fun runProgram(programBytes: ByteArray) {
        val intInstructions = parseIntInstructions(programBytes)

        memory.addAll(intInstructions)

        var run = true

        while (run && ip in 0 until memory.size) {
            if (OpCode.representsOpCode(memory[ip])) {
                val opCode = OpCode.fromInt(memory[ip])
                run = processOpCode(opCode)
            } else {
                break
            }
        }
    }

    fun getRegisterValues() = registers.toList()

    // Implementation

    private fun processOpCode(opCode: OpCode): Boolean {
        if (REGISTER_7_INSTRUCTION_NUMBERS.contains(ip)) {
            println("Hit line $ip")
        }

        when (opCode) {
            HALT -> {
                ip += 1

                return false
            }

            SET -> {
                val target  = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand = Operand.fromInt(memory[ip + 2])

                registers[target.index] = operandToInt(operand)
                ip += 3

                return true
            }

            PUSH -> {
                val operand = Operand.fromInt(memory[ip + 1])

                stack.addFirst(operandToInt(operand))
                ip += 2

                return true
            }

            POP -> {
                val value  = stack.pollFirst() ?: return false
                val target = Operand.fromInt(memory[ip + 1]) as? Register ?: return false

                registers[target.index] = value
                ip += 2

                return true
            }

            EQ -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                val equal = operandToInt(operand2) == operandToInt(operand3)

                registers[target.index] = if (equal) 1 else 0
                ip += 4

                return true
            }

            GT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                val greaterThan = operandToInt(operand2) > operandToInt(operand3)

                registers[target.index] = if (greaterThan) 1 else 0
                ip += 4

                return true
            }

            JMP -> {
                val operand = Operand.fromInt(memory[ip + 1])

                ip = operandToInt(operand)

                return true
            }

            JT -> {
                val operand1 = Operand.fromInt(memory[ip + 1])
                val operand2 = Operand.fromInt(memory[ip + 2])

                val jump = operandToInt(operand1) != 0

                ip = if (jump) operandToInt(operand2) else ip + 3

                return true
            }

            JF -> {
                val operand1 = Operand.fromInt(memory[ip + 1])
                val operand2 = Operand.fromInt(memory[ip + 2])

                val jump = operandToInt(operand1) == 0

                ip = if (jump) operandToInt(operand2) else ip + 3

                return true
            }

            ADD -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                registers[target.index] = (operandToInt(operand2) + operandToInt(operand3)) % 32768
                ip += 4

                return true
            }

            MULT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                registers[target.index] = (operandToInt(operand2) * operandToInt(operand3)) % 32768
                ip += 4

                return true
            }

            MOD -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                registers[target.index] = operandToInt(operand2).rem(operandToInt(operand3))
                ip += 4

                return true
            }

            AND -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                registers[target.index] = operandToInt(operand2) and operandToInt(operand3)
                ip += 4

                return true
            }

            OR -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])
                val operand3 = Operand.fromInt(memory[ip + 3])

                registers[target.index] = operandToInt(operand2) or operandToInt(operand3)
                ip += 4

                return true
            }

            NOT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])

                registers[target.index] = operandToInt(operand2).inv() and ((1 shl 15) - 1)
                ip += 3

                return true
            }

            RMEM -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2])

                registers[target.index] = memory[operandToInt(operand2)]
                ip += 3

                return true
            }

            WMEM -> {
                val operand1 = Operand.fromInt(memory[ip + 1])
                val operand2 = Operand.fromInt(memory[ip + 2])

                memory[operandToInt(operand1)] = operandToInt(operand2)
                ip += 3

                return true
            }

            CALL -> {
                val operand = Operand.fromInt(memory[ip + 1])

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
                val operand = Operand.fromInt(memory[ip + 1])

                actor.handleOutput(operandToInt(operand).toChar())
                ip += 2

                return true
            }

            IN -> {
                val target = Operand.fromInt(memory[ip + 1]) as? Register ?: return false

                if (lastInput.isEmpty()) {
                    val input = actor.getInput()

                    val regex = Regex("register\\[7]\\s*=\\s*(\\d+)")
                    val matchResult = regex.matchEntire(input.trim())

                    if (matchResult != null) {
                        registers[7] = matchResult.groups[1]!!.value.toInt()
                        return true
                    }

                    input.forEach { lastInput.add(it) }
                    lastInput.add('\n')
                }

                registers[target.index] = lastInput.poll().toInt()
                ip += 2

                return true
            }

            NOOP -> {
                ip += 1

                return true
            }
        }
    }

    private fun operandToInt(operand: Operand) = when (operand) {
        is Number   -> operand.value
        is Register -> registers[operand.index]
    }

}
