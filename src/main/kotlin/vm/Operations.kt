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

    override fun toString() = super.toString().toLowerCase()

}

sealed class Operand {

    class Number(val value: Int) : Operand() {
        override fun toString() = value.toString()
    }

    class Register(val index: Int) : Operand() {
        override fun toString() = "register[$index]"
    }

    companion object {
        fun fromInt(int: Int): Operand? = when (int) {
            in 0..MAX_INT -> {
                Number(value = int)
            }

            in FIRST_REGISTER_INSTRUCTION..LAST_REGISTER_INSTRUCTION -> {
                Register(index = int - FIRST_REGISTER_INSTRUCTION)
            }

            else -> {
                null
            }
        }
    }

}
