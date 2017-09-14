import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class VM(
        private val memory: MutableList<Int> = mutableListOf(),
        private val registers: IntArray = IntArray(8) { 0 },
        private val stack: Deque<Int> = ArrayDeque<Int>(),
        private var instructionPointer: Int = 0,
        private val loggingEnabled: Boolean = false
) {

    fun runProgram(programName: String) {
        val unsignedBytes = getUnsignedBytes(programName) ?: return

        maybeLog("Input parsed into unsigned bytes: $unsignedBytes")

        val unsigned16BitInts = parseTo16BitInts(unsignedBytes)
        maybeLog("Input parsed into unsigned 16-bit integers: $unsigned16BitInts")

        memory.addAll(unsigned16BitInts)
        maybeLog("Input loaded into memory.")
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
    private fun parseTo16BitInts(unsignedBytes: List<Int>): List<Int> {
        val result = mutableListOf<Int>()

        for (i in 0 until unsignedBytes.size step 2) {
            val lowByte  = unsignedBytes[i]
            val highByte = unsignedBytes[i + 1]
            result.add((highByte shl 8) + lowByte)
        }

        return result
    }

    private fun maybeLog(statement: String) {
        if (loggingEnabled) println(statement)
    }

    private fun maybePrintState() {
        maybeLog("Current VM state:")
        maybeLog("Registers: ${Arrays.toString(registers)}")
        maybeLog("Stack: $stack")
    }

}
