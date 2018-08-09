package teleportation

class EnergyLevelCalculator {

  private val map: MutableMap<Pair<Int, Int>, Pair<Int, Int>> = mutableMapOf()

  fun comp() {
    println(v(1, 0, 1))
    println(v2(1, 0, 1))

    println(v(2, 0, 1))
    println(v2(2, 0, 1))

    println(v(2, 1, 1))
    println(v2(2, 1, 1))

    println(v(3, 0, 1))
    println(v2(3, 0, 1))

    println(v2(4, 1, 1))
  }

  private fun v(r0: Int, r1: Int, r7: Int): Pair<Int, Int> {
    while (true) {
      map[Pair(r0, r1)]?.let { return it }

      val result = when {
        r0 == 0 -> {
          Pair(r1 + 1, r1)
        }
        r1 == 0 -> {
          v(r0 - 1, r7, r7)
        }
        else -> {
          v(r0 - 1, v(r0, r1 - 1, r7).first, r7)
        }
      }

      map[Pair(r0, r1)] = result
      return result
    }
  }

  // One level of recursion removed
  private fun v2(r0: Int, r1: Int, r7: Int): Pair<Int, Int> {
    var r0local = r0
    var r1local = r1

    while (true) {
      if (r0local == 0) {
        return Pair(r1local + 1, r1local)
      } else if (r1local == 0) {
        r0local--
        r1local = r7
        continue
      } else {
        r1local = v2(r0local, r1local - 1, r7).first // Important to call before decrementing r0local
        r0local--
        continue
      }
    }
  }

}
