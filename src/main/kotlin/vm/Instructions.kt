package vm

/**
 * Converts an array of bytes representing a program into a list of 16-bit unsigned integer instructions.
 */
fun parseIntInstructions(programBytes: ByteArray): List<Int> {
    val bytesAsUnsignedInts = programBytes.map(Byte::toUnsignedInt)

    val result = mutableListOf<Int>()

    for (i in 0 until bytesAsUnsignedInts.size step 2) {
        val lowByteInt  = bytesAsUnsignedInts[i]
        val highByteInt = bytesAsUnsignedInts[i + 1]
        result.add((highByteInt shl 8) + lowByteInt)
    }

    return result
}
