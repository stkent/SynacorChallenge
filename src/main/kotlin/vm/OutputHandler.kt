package vm

import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream

interface OutputHandler {
  fun handleOutput(output: String)
  fun performCleanup()
}

class StdOutOutputHandler: OutputHandler {

  override fun handleOutput(output: String) {
    print(output)
  }

  override fun performCleanup() {
    // No implementation required.
  }

}


class FileOutputHandler(fileLocation: String): OutputHandler {

  private val filePrintWriter = PrintWriter(FileOutputStream(File(fileLocation)))

  override fun handleOutput(output: String) {
    filePrintWriter.println(output)
  }

  override fun performCleanup() {
    filePrintWriter.close()
  }

}
