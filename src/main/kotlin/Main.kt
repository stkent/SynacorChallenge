object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        VM(loggingEnabled = true).runProgram(programName = "sample.bin")
    }

}
