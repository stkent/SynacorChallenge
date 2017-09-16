package vm

enum class OpCode(val operandCount: Int) {
    HALT(0),
    SET(2),
    PUSH(1),
    POP(1),
    EQ(3),
    GT(3),
    JMP(1),
    JT(2),
    JF(2),
    ADD(3),
    MULT(3),
    MOD(3),
    AND(3),
    OR(3),
    NOT(2),
    RMEM(2),
    WMEM(2),
    CALL(1),
    RET(0),
    OUT(1),
    IN(1),
    NOOP(0);

    companion object {
        fun fromInt(int: Int): OpCode? {
            return try {
                OpCode.values()[int]
            } catch (exception: ArrayIndexOutOfBoundsException) {
                null
            }
        }
    }
}

sealed class Operand {

    class Number(val value: Int) : Operand() {
        override fun toString() = value.toString()
    }

    class Register(val index: Int) : Operand() {
        override fun toString() = "REG[$index]"
    }

    companion object {
        fun fromInt(int: Int): Operand? = when (int) {
            in 0..32767     -> Number(value = int)
            in 32768..32775 -> Register(index = int - 32768)
            else            -> null
        }
    }

}