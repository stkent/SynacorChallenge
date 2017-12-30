object DummyActor : Actor {

  override fun handleOutput(char: Char) {
    // This method intentionally left blank.
  }

  override fun getInput(): String {
    throw NotImplementedError("Dummy Actor is not designed to respond to input requests.")
  }

}
