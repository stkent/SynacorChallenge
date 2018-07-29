package vm

import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream

interface OutputHandler {
  fun print(output: String)
  fun println(output: String)
}

class SysOutOutputHandler: OutputHandler {

  override fun print(output: String) {
    System.`out`.println(output)
  }

  override fun println(output: String) {
    print(output)
    System.`out`.println()
  }

}

class FileOutputHandler(fileLocation: String): OutputHandler {

  private val filePrintWriter = PrintWriter(FileOutputStream(File(fileLocation)))

  override fun print(output: String) {
    filePrintWriter.print(output)
  }

  override fun println(output: String) {
    print(output)
    filePrintWriter.println()
  }

  fun performCleanup() {
    filePrintWriter.close()
  }

}
