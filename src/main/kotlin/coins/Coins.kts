// Mapping between coin description and value.
val coins = mapOf(
        Pair(2, "red"),
        Pair(3, "corroded"),
        Pair(5, "shiny"),
        Pair(7, "concave"),
        Pair(9, "blue")
)

/*
 * Function described by the monument. We wish to find the sequence of coins whose values, when passed as inputs to this
 * function, produce the result 399.
 */
fun function(c1: Int, c2: Int, c3: Int, c4: Int, c5: Int): Int =
        c1 + c2 * Math.pow(c3.toDouble(), 2.0).toInt() + Math.pow(c4.toDouble(), 3.0).toInt() - c5

// From https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit
fun <T> List<T>.permutations(): Set<List<T>> = when {
    isEmpty() -> setOf()
    size == 1 -> setOf(listOf(first()))
    else -> {
        val element = first()
        drop(1).permutations()
                .flatMap { sublist -> (0..sublist.size).map { i -> sublist.plusAt(i, element) } }
                .toSet()
    }
}

// From https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit
fun <T> List<T>.plusAt(index: Int, element: T): List<T> = when (index) {
    !in 0..size -> throw Error("Cannot put at index $index because size is $size")
    0 -> listOf(element) + this
    size -> this + element
    else -> dropLast(size - index) + element + drop(index)
}

// Compute and print all viable permutations.
coins.keys
        .toList()
        .permutations()
        .filter { (c1, c2, c3, c4, c5) -> function(c1, c2, c3, c4, c5) == 399 }
        .forEach { permutation ->
            val readablePermutation = permutation.map { value -> coins[value] }
            println("Viable permutation found: $readablePermutation")
        }
