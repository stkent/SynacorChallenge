import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val binaryBytes = getBinaryBytes("sample.bin")

        if (binaryBytes != null) {
            println("Input bytes:")
            println(Arrays.toString(binaryBytes))
        } else {
            println("Error reading input file.")
        }
    }

    private fun getBinaryBytes(binaryFileName: String): ByteArray? {
        val fileName = javaClass.classLoader.getResource(binaryFileName)?.file
        return if (fileName != null) Files.readAllBytes(Paths.get(fileName)) else null
    }

}
