import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class VM(
        private val memory: MutableList<Int> = mutableListOf(),
        private val registers: IntArray = IntArray(8) { 0 },
        private val stack: Deque<Int> = ArrayDeque<Int>(),
        private var instructionPointer: Int = 0
) {

    fun runProgram(programName: String, log: Boolean = false) {
        val unsignedBytes = getUnsignedBytes(programName) ?: return

        if (log) { println("Input parsed into unsigned bytes: $unsignedBytes") }

        val unsigned16BitInts = parseTo16BitInts(unsignedBytes)
        if (log) { println("Input parsed into unsigned 16-bit integers: $unsigned16BitInts") }

        memory.addAll(unsigned16BitInts)
        if (log) { println("Input loaded into memory.") }
        if (log) { printState() }
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
    private fun parseTo16BitInts(unsignedBytes: List<Int>): List<Int> {
        val result = mutableListOf<Int>()

        for (i in 0 until unsignedBytes.size step 2) {
            val lowByte  = unsignedBytes[i]
            val highByte = unsignedBytes[i + 1]
            result.add((highByte shl 8) + lowByte)
        }

        return result
    }

    private fun printState() {
        println("Current VM state:")
        println("Registers: ${Arrays.toString(registers)}")
        println("Stack: $stack")
    }

}
