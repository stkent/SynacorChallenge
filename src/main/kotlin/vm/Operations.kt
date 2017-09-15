package vm

enum class OpCode {
    HALT,
    SET,
    PUSH,
    POP,
    EQ,
    GT,
    JMP,
    JT,
    JF,
    ADD,
    MULT,
    MOD,
    AND,
    OR,
    NOT,
    RMEM,
    WMEM,
    CALL,
    RET,
    OUT,
    IN,
    NOOP;

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
