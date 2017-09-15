package vm

import vm.OpCode.*

class Decompiler {

    fun getReadableInstructions(programBytes: ByteArray) {
        val intInstructions = getIntInstructions(programBytes)

        var index = 0

        while (index < intInstructions.size) {
            val opCode = OpCode.fromInt(intInstructions[index])!!
            println(opCode)

            when (opCode) {
                HALT -> TODO()
                SET -> TODO()
                PUSH -> TODO()
                POP -> TODO()
                EQ -> TODO()
                GT -> TODO()
                JMP -> TODO()
                JT -> TODO()
                JF -> TODO()
                ADD -> TODO()
                MULT -> TODO()
                MOD -> TODO()
                AND -> TODO()
                OR -> TODO()
                NOT -> TODO()
                RMEM -> TODO()
                WMEM -> TODO()
                CALL -> TODO()
                RET -> TODO()
                OUT -> TODO()
                IN -> TODO()
                NOOP -> TODO()
            }
        }
    }

    private fun printOperands(intInstructions: List<Int>) {

    }

}
