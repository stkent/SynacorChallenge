import vm.VM
import java.nio.file.Path
import java.nio.file.Paths

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        getPathToProgram("challenge.bin")?.let { path ->
            VM(actor = BootstrappedActor).runProgram(pathToProgram = path)
        }
    }

    private fun getPathToProgram(programName: String): Path? {
        val fileName = javaClass.classLoader.getResource(programName)?.file ?: return null
        return Paths.get(fileName)
    }

}
