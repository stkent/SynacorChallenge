import vm.Decompiler
import vm.VM
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        // Name of a binary file residing in the resources directory.
        val fileName = "challenge"

        val programBytes = readProgramBytes("$fileName.bin")

        print("""
              Choose an option:

              [1] Run program
              [2] Decompile program

              > """.trimIndent())

        val option = Scanner(System.`in`).nextLine()!!

        when (option) {
            "1" -> VM(actor = InteractiveActor).runProgram(programBytes)
            "2" -> Decompiler().decompile(programBytes, "${fileName}_decompiled.txt")
            else -> println("Option not recognized. Exiting.")
        }
    }

    private fun readProgramBytes(programName: String): ByteArray {
        val fileName = javaClass.classLoader.getResource(programName)!!.file
        val filePath = Paths.get(fileName)
        return Files.readAllBytes(filePath)
    }

}
