import vm.Decompiler
import vm.VM
import java.nio.file.Files
import java.nio.file.Paths

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val programBytes = readProgramBytes("challenge.bin")

        Decompiler().decompile(programBytes)
        VM(actor = InteractiveActor).runProgram(programBytes)
    }

    private fun readProgramBytes(programName: String): ByteArray {
        val fileName = javaClass.classLoader.getResource(programName)!!.file
        val filePath = Paths.get(fileName)
        return Files.readAllBytes(filePath)
    }

}
