package vm

interface Display {
  fun printChar(c: Char)
  fun printLine(s: String)
}

class SysOutDisplay: Display {

  override fun printChar(c: Char) {
    System.`out`.print(c)
  }

  override fun printLine(s: String) {
    System.`out`.println(s)
  }

}
