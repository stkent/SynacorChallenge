import OpCode.*
import Operand.Register
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class VM(
        private val memory:    MutableList<Int> = mutableListOf(),
        private val registers: IntArray         = IntArray(8) { 0 },
        private val stack:     Deque<Int>       = ArrayDeque<Int>(),
        private val log:       Boolean          = false,
        private var ip:        Int              = 0 // Instruction pointer
) {

    fun runProgram(programName: String) {
        val unsignedBytes = getUnsignedBytes(programName) ?: return

        maybeLog("Input parsed into unsigned bytes: $unsignedBytes")

        val unsigned16BitInts = to16BitInts(unsignedBytes)
        maybeLog("Input parsed into unsigned 16-bit integers: $unsigned16BitInts")

        memory.addAll(unsigned16BitInts)
        maybeLog("Input loaded into memory.")
        maybePrintState()

        var run = true

        while (run && ip in 0 until memory.size) {
            maybeLog("Handling instruction at index $ip")

            val opCode = OpCode.fromInt(memory[ip]) ?: break
            run = processOpCode(opCode)
        }

        maybePrintState()
    }

    /**
     * fill me in
     */
    private fun getUnsignedBytes(programName: String): List<Int>? {
        val fileName = javaClass.classLoader.getResource(programName)?.file ?: return null
        return Files.readAllBytes(Paths.get(fileName)).map(Byte::toUnsignedInt)
    }

    /**
     * Converts pairs of bytes (arranged in little-endian format) into 16-bit unsigned integers.
     */
    private fun to16BitInts(unsignedBytes: List<Int>): List<Int> {
        val result = mutableListOf<Int>()

        for (i in 0 until unsignedBytes.size step 2) {
            val lowByte  = unsignedBytes[i]
            val highByte = unsignedBytes[i + 1]
            result.add((highByte shl 8) + lowByte)
        }

        return result
    }

    private fun processOpCode(opCode: OpCode): Boolean {
        when (opCode) {
            HALT -> {
                ip += 1

                return false
            }

            SET -> {
                val target  = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand = Operand.fromInt(memory[ip + 2]) ?: return false

                registers[target.index] = operandToInt(operand)
                ip += 3

                return true
            }

            PUSH -> {
                val operand = Operand.fromInt(memory[ip + 1]) ?: return false

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
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                val equal = operandToInt(operand2) == operandToInt(operand3)

                registers[target.index] = if (equal) 1 else 0
                ip += 4

                return true
            }

            GT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                val greaterThan = operandToInt(operand2) > operandToInt(operand3)

                registers[target.index] = if (greaterThan) 1 else 0
                ip += 4

                return true
            }

            JMP -> {
                val operand = Operand.fromInt(memory[ip + 1]) ?: return false

                ip = operandToInt(operand)

                return true
            }

            JT -> {
                val operand1 = Operand.fromInt(memory[ip + 1]) ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false

                val jump = operandToInt(operand1) != 0

                ip = if (jump) operandToInt(operand2) else ip + 3

                return true
            }

            JF -> {
                val operand1 = Operand.fromInt(memory[ip + 1]) ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false

                val jump = operandToInt(operand1) == 0

                ip = if (jump) operandToInt(operand2) else ip + 3

                return true
            }

            ADD -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                registers[target.index] = (operandToInt(operand2) + operandToInt(operand3)) % 32768
                ip += 4

                return true
            }

            MULT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                registers[target.index] = (operandToInt(operand2) * operandToInt(operand3)) % 32768
                ip += 4

                return true
            }

            MOD -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                registers[target.index] = operandToInt(operand2).rem(operandToInt(operand3))
                ip += 4

                return true
            }

            AND -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                registers[target.index] = operandToInt(operand2) and operandToInt(operand3)
                ip += 4

                return true
            }

            OR -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false
                val operand3 = Operand.fromInt(memory[ip + 3]) ?: return false

                registers[target.index] = operandToInt(operand2) or operandToInt(operand3)
                ip += 4

                return true
            }

            NOT -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false

                registers[target.index] = operandToInt(operand2).inv()
                ip += 3

                return true
            }

            RMEM -> {
                val target   = Operand.fromInt(memory[ip + 1]) as? Register ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false

                registers[target.index] = memory[operandToInt(operand2)]
                ip += 3

                return true
            }

            WMEM -> {
                val operand1 = Operand.fromInt(memory[ip + 1]) ?: return false
                val operand2 = Operand.fromInt(memory[ip + 2]) ?: return false

                memory[operandToInt(operand1)] = operandToInt(operand2)
                ip += 3

                return true
            }

            CALL -> {
                val operand = Operand.fromInt(memory[ip + 1]) ?: return false

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
                val operand = Operand.fromInt(memory[ip + 1]) ?: return false

                print(operandToInt(operand).toChar())
                ip += 2

                return true
            }

            IN -> TODO()

            NOOP -> {
                ip += 1

                return true
            }
        }
    }

    private fun operandToInt(operand: Operand) = when (operand) {
        is Operand.Number   -> operand.value
        is Register -> registers[operand.index]
    }

    // Logging methods

    private fun maybeLog(statement: String) {
        if (log) println(statement)
    }

    private fun maybePrintState() {
        maybeLog("Current VM state:")
        maybeLog("Registers: ${Arrays.toString(registers)}")
        maybeLog("Stack: $stack")
    }

}
