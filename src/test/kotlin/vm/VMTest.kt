package vm

import DummyActor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VMTest {

  // todo: fixme
  @Test
  fun test() {
    val vm = VM(
        actor = DummyActor(),
        registersSeed = { 0 },
        stackSeed = emptyList(),
        ip = 0)

//    vm.runProgram(listOf(14, 32768, 14000))

    val register1Value = vm.getRegisterValues().first()
    assertEquals(17875, register1Value)
  }

}
