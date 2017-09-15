import java.util.*

interface Actor {
    fun handleOutput(char: Char)
    fun getInput(): String
}

object InteractiveActor : Actor {

    override fun handleOutput(char: Char) = print(char)
    override fun getInput() = Scanner(System.`in`).nextLine()!!

}

object AutomatedActor : Actor {

    override fun handleOutput(char: Char) = kotlin.io.print(char)

    private val initialInstructions = ArrayDeque<String>().apply {
        add("doorway")
        add("north")
        add("north")
        add("bridge")
        add("continue")
        add("down")
        add("east")
        add("take empty lantern")
        add("west")
        add("west")
        add("passage")
        add("ladder")
    }

    override fun getInput(): String {
        if (initialInstructions.peek() != null) {
            val nextInstruction = initialInstructions.poll()
            print(nextInstruction)
            return nextInstruction
        } else {
            val input = InteractiveActor.getInput()
            print(input)
            return input
        }
    }

}
