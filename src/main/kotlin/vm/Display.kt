package vm

interface Display {
  fun print(char: Char)
}

class SysOutDisplay: Display {

  override fun print(char: Char) {
    System.`out`.print(char)
  }

}
