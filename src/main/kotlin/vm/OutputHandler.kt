package vm

import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream

interface OutputHandler {
  fun handleOutput(output: String)
}

class StdOutOutputHandler: OutputHandler {

  override fun handleOutput(output: String) {
    println(output)
  }

}

class FileOutputHandler(fileLocation: String): OutputHandler {

  private val filePrintWriter = PrintWriter(FileOutputStream(File(fileLocation)))

  override fun handleOutput(output: String) {
    filePrintWriter.println(output)
  }

  fun performCleanup() {
    filePrintWriter.close()
  }

}
