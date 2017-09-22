import java.util.*

interface Actor {
    fun handleOutput(char: Char)
    fun getInput(): String
}

object InteractiveActor : Actor {

    override fun handleOutput(char: Char) = print(char)
    override fun getInput() = Scanner(System.`in`).nextLine()!!

}

object BootstrappedActor : Actor {

    override fun handleOutput(char: Char) = print(char)

    /**
     *
     */
    private val initialInstructions = ArrayDeque<String>().apply {
        add("take tablet")
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
        add("west")
        add("south")
        add("north")
        add("take can")
        add("use can")
        add("west")
        add("ladder")
        add("use lantern")
        add("darkness")
        add("continue")
        add("west")
        add("west")
        add("west")
        add("west")
        add("north")
        add("take red coin")
        add("north")
        add("east")
        add("take concave coin")
        add("down")
        add("take corroded coin")
        add("up")
        add("west")
        add("west")
        add("take blue coin")
        add("up")
        add("take shiny coin")
        add("down")
        add("east")
        add("use blue coin")
        add("use red coin")
        add("use shiny coin")
        add("use concave coin")
        add("use corroded coin")
        add("north")
        add("take teleporter")
        add("use teleporter")
        add("take business card")
        add("take strange book")
        add("register[7] = 3")
    }

    override fun getInput(): String {
        @Suppress("LiftReturnOrAssignment")
        if (initialInstructions.peek() != null) {
            val nextInstruction = initialInstructions.poll()
            println(nextInstruction)
            return nextInstruction
        } else {
            return InteractiveActor.getInput()
        }
    }

}
