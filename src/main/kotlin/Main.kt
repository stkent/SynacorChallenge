import vm.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object Main {

  private val screenPrinter = ScreenPrinter()

  @JvmStatic
  fun main(args: Array<String>) {
    // Name of a binary file residing in the resources directory.
    val fileName = "challenge"

    val programBytes = readProgramBytes("$fileName.bin")

    screenPrinter.printLine(
        """
        Choose an option:

        [1] Run challenge.bin
        [2] Decompile challenge.bin
        [3] Solve coins puzzle

        > """.trimIndent())

    val option = Scanner(System.`in`).nextLine()!!

    when (option.trim()) {
      "1" -> {
        val printer = ScreenPrinter()
        val vm = VM(actor = BootstrappedActor(), outputPrinter = printer)
        vm.runProgram(programBytes)
      }

      "2" -> {
        val printer = FilePrinter(fileLocation = "out/${fileName}_decompiled.txt")
        Decompiler().decompile(programBytes, instructionPrinter = printer)
        printer.performCleanup()
      }

      "3" -> {
        val coinPuzzleSolver = CoinPuzzleSolver()
        val coinPuzzleSolutions = coinPuzzleSolver.solve()

        screenPrinter.printLine("Sequences of coins that solve the puzzle:")

        coinPuzzleSolutions.forEach { solution ->
          screenPrinter.printLine(solution.joinToString(prefix = "(", postfix = ")"))
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
