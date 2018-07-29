class DummyActor : Actor {
  override fun getInputLine(): String {
    throw NotImplementedError("Dummy Actor is not designed to respond to input requests.")
  }
}
