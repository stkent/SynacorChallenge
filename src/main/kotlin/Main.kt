import coins.CoinPuzzleSolver
import teleportation.EnergyLevelCalculator
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
        s = """
            Choose an option:

            [1] Run challenge.bin
            [2] Decompile challenge.bin
            [3] Solve coins puzzle

            > """.trimIndent(),
        trailingNewline = false)

    val option = Scanner(System.`in`).nextLine()!!

    screenPrinter.printLine("")

    when (option.trim()) {
      "1" -> {
        val vm = VM(actor = BootstrappedActor(), outputPrinter = screenPrinter)
        vm.runProgram(programBytes)
      }

      "2" -> {
        val outputFilePrinter = FilePrinter(fileLocation = "out/${fileName}_decompiled.txt")
        Decompiler().decompile(programBytes, printer = outputFilePrinter)
        outputFilePrinter.performCleanup()
      }

      "3" -> {
        val coinPuzzleSolver = CoinPuzzleSolver()
        val coinPuzzleSolutions = coinPuzzleSolver.solve()

        screenPrinter.printLine("Sequences of coins that solve the puzzle:")

        coinPuzzleSolutions.forEach { solution ->
          screenPrinter.printLine(solution.joinToString(prefix = "(", postfix = ")"))
        }
      }

      "4" -> {
        EnergyLevelCalculator().comp()
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
