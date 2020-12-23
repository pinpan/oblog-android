package androidx.lifecycle

/**
 * OblogViewModelStore is needed becasue the default ViewModelStore set by the ViewModelProvider
 * can't be used from outside the ViewModelProvider package: internam mMap is final private and
 * access methods get and put have package visibility only.
 */
class OblogViewModelStore<T : ViewModel> : ViewModelStore() {
    /**
     * We need to duplicate the map in order to allow access from both, the ViewModelProvider and our ViewModelFactory,
     * since the default ViewModelStore get and put are not only package private but also final and can't be overridden.
     */
    private val models = HashMap<String, ViewModel>()

    fun <T : ViewModel> putModel(key: String, viewModel: T) {
        super.put(key, viewModel)
        models.put(key, viewModel)
    }

    fun getModel(key: String): ViewModel? =
            with(key) {
                return models.get(key)
            }
}
