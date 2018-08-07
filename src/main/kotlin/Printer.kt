import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream

interface Printer {
  fun printChar(c: Char)
  fun printLine(s: String, trailingNewline: Boolean = true)
}

class ScreenPrinter: Printer {

  override fun printChar(c: Char) = print(c)

  override fun printLine(s: String, trailingNewline: Boolean) {
    if (trailingNewline) { println(s) } else { print(s) }
  }
}

class FilePrinter(fileLocation: String): Printer {

  private val filePrintWriter = PrintWriter(FileOutputStream(File(fileLocation)))

  override fun printChar(c: Char) = filePrintWriter.print(c)

  override fun printLine(s: String, trailingNewline: Boolean) {
    if (trailingNewline) { filePrintWriter.println(s) } else { filePrintWriter.print(s) }
  }

  fun performCleanup() = filePrintWriter.close()

}
