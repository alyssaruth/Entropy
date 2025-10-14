package bean

data class ComboBoxItem<E>(val hiddenData: E, val visibleData: String, val enabled: Boolean) {
    override fun toString() =
        if (enabled) visibleData else "<html><font color=\"gray\">$visibleData</font></html>"
}
