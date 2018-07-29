import vm.Decompiler
import vm.VM
import java.io.File
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
      "1" -> VM(actor = BootstrappedActor).runProgram(programBytes)

      "2" -> {
        File("${fileName}_decompiled.txt").printWriter().use { writer ->
          Decompiler().decompile(programBytes, writer)
        }
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
