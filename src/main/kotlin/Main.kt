import vm.*
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

          [1] Run challenge.bin
          [2] Decompile challenge.bin

          > """.trimIndent())

    val option = Scanner(System.`in`).nextLine()!!

    when (option.trim()) {
      "1" -> {
        val vm = VM(actor = BootstrappedActor())
        vm.runProgram(programBytes)
      }

      "2" -> {
        val outputHandler = FileInstructionPrinter("out/${fileName}_decompiled.txt")
        Decompiler().decompile(programBytes, outputHandler)
        outputHandler.performCleanup()
      }

      else -> println("Option not recognized. Exiting.")
    }
  }

  private fun readProgramBytes(programName: String): ByteArray {
    val fileName = javaClass.classLoader.getResource(programName)!!.file
    val filePath = Paths.get(fileName)
    return Files.readAllBytes(filePath)
  }

}
