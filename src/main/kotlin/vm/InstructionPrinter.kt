package vm

import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream

interface InstructionPrinter {
  fun println(output: String)
}

class FileInstructionPrinter(fileLocation: String): InstructionPrinter {

  private val filePrintWriter = PrintWriter(FileOutputStream(File(fileLocation)))

  override fun println(output: String) {
    filePrintWriter.println(output)
  }

  fun performCleanup() {
    filePrintWriter.close()
  }

}
