// From https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit
fun <T> List<T>.permutations(): Set<List<T>> = when {
  isEmpty() -> {
    setOf()
  }

  size == 1 -> {
    setOf(listOf(first()))
  }

  else -> {
    val element = first()
    drop(1)
        .permutations()
        .flatMap { sublist -> (0..sublist.size).map { i -> sublist.plusAt(i, element) } }
        .toSet()
  }
}

// From https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit
fun <T> List<T>.plusAt(index: Int, element: T): List<T> = when (index) {
  !in 0..size -> throw Error("Cannot put at index $index because size is $size")
  0           -> listOf(element) + this
  size        -> this + element
  else        -> dropLast(size - index) + element + drop(index)
}
