package coins

import permutations

class CoinPuzzleSolver {

  /**
   * Coins collected from around the monument.
   */
  private val coins = mapOf(
      Pair(2, "red"),
      Pair(3, "corroded"),
      Pair(5, "shiny"),
      Pair(7, "concave"),
      Pair(9, "blue")
  )

  /**
   * Returns all sequences of coins that satisfy obj(p) = 399.
   */
  fun solve(): List<List<String>> {
    return coins.keys
        .toList()
        .permutations()
        .filter { (c1, c2, c3, c4, c5) -> obj(c1, c2, c3, c4, c5) == 399 }
        .map { permutation ->
          return@map permutation.map { value -> coins[value]!! }
        }
  }

  /**
   * Objective function described by the monument.
   */
  private fun obj(c1: Int, c2: Int, c3: Int, c4: Int, c5: Int) = c1 + c2*(c3*c3) + (c4*c4*c4) - c5

}
