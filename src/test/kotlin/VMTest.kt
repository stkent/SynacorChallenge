import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import vm.VM
import java.nio.file.Paths

class VMTest {

    @Test
    fun test() {
        val vm = VM(
                registersSeed = { 0 },
                stackSeed = emptyList(),
                ip = 0)

        val fileName = javaClass.classLoader.getResource("not.bin")?.file ?: fail("Test file not found!")
        vm.runProgram(Paths.get(fileName))

        val register1Value = vm.getRegisterValues().first()
        assertEquals(17875, register1Value)
    }

}
