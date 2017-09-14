import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val unsignedInts = getUnsignedInts("sample.bin")

        if (unsignedInts != null) {
            println("Input bytes (interpreted as unsigned ints):")
            println(Arrays.toString(unsignedInts))
        } else {
            println("Error reading input file.")
        }
    }

    private fun getUnsignedInts(binaryFileName: String): IntArray? {
        val fileName = javaClass.classLoader.getResource(binaryFileName)?.file ?: return null
        return Files.readAllBytes(Paths.get(fileName))
                .map(Byte::toUnsignedInt)
                .toIntArray()
    }

//    private fun parseTo16BitInts(unsignedInts: IntArray): IntArray {
//
//    }

}
