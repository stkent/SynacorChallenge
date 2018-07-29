class DummyActor : Actor {
  override fun getInput(): String {
    throw NotImplementedError("Dummy Actor is not designed to respond to input requests.")
  }
}
