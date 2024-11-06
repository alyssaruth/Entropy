package util

data class ReleaseAsset(val id: Long, val name: String, val size: Long)

data class UpdateMetadata(val tag_name: String, val assets: List<ReleaseAsset>) {
    fun toScriptArgs(): String {
        val asset = assets.first()
        return "${asset.size} ${tag_name} ${asset.name} ${asset.id}"
    }
}
