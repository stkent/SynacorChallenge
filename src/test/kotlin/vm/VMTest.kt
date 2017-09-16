package vm

import DummyActor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VMTest {

    @Test
    fun test() {
        val vm = VM(
                registersSeed = { 0 },
                stackSeed = emptyList(),
                ip = 0,
                actor = DummyActor)

//        vm.runProgram(listOf(14, 32768, 14000))

        val register1Value = vm.getRegisterValues().first()
        assertEquals(17875, register1Value)
    }

}
