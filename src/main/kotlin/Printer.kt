interface Printer {
    fun print(char: Char)
}

object SystemOutPrinter : Printer {

    override fun print(char: Char) {
        print(char)
    }

}
