package vm

interface Printer {
    fun print(char: Char)
}

object SystemOutPrinter : Printer {

    override fun print(char: Char) {
        kotlin.io.print(char)
    }

}
