import vm.Decompiler
import vm.VM
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val programBytes = readProgramBytes("challenge.bin")

        println("""
            Choose an option:

            [1] Run program")
            [2] Decompile program")

            > """.trimIndent())

        val option = Scanner(System.`in`).nextLine()!!

        when (option) {
            "1" -> VM(actor = InteractiveActor).runProgram(programBytes)
            "2" -> Decompiler().decompile(programBytes)
            else -> println("Option not recognized. Exiting.")
        }
    }

    private fun readProgramBytes(programName: String): ByteArray {
        val fileName = javaClass.classLoader.getResource(programName)!!.file
        val filePath = Paths.get(fileName)
        return Files.readAllBytes(filePath)
    }

}
