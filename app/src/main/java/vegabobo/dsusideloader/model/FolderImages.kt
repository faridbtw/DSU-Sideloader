package vegabobo.dsusideloader.model

import java.util.Locale

interface FolderDocument {
    val uriString: String
    val name: String?
    val isDirectory: Boolean
    val isFile: Boolean
    val size: Long
    fun children(): List<FolderDocument>
}

enum class FolderImageFormat { IMAGE, GZIP, XZ, DSU_ZIP }

data class FolderImage(
    val uriString: String,
    val filename: String,
    val relativePath: String,
    val size: Long,
    val format: FolderImageFormat,
)

data class FolderImageScan(
    val folderName: String,
    val images: List<FolderImage>,
    val limited: Boolean = false,
)

fun folderImageFormat(filename: String, supportsZip: Boolean): FolderImageFormat? {
    val normalized = filename.lowercase(Locale.ROOT)
    val stem = normalized.removeSuffix(".gz").removeSuffix(".gzip").removeSuffix(".xz").removeSuffix(".img")
        .removeSuffix("_a").removeSuffix("_b").removeSuffix("_gsi")
    // These partitions are not standalone Generic System Images.
    if (stem in setOf("userdata", "vendor", "boot", "init_boot", "vendor_boot", "vbmeta", "super", "recovery", "dtbo", "odm", "product", "system_ext", "cache", "metadata", "persist", "scratch", "super_empty", "vendor_dlkm", "odm_dlkm", "system_dlkm", "vbmeta_system", "vbmeta_vendor")) return null
    return when (normalized.substringAfterLast('.', "")) {
        "img" -> FolderImageFormat.IMAGE
        "gz", "gzip" -> FolderImageFormat.GZIP
        "xz" -> FolderImageFormat.XZ
        "zip" -> if (supportsZip) FolderImageFormat.DSU_ZIP else null
        else -> null
    }
}

class FolderImageScanner(
    private val maxDepth: Int = 4,
    private val maxEntries: Int = 3000,
    private val maxImages: Int = 200,
) {
    init {
        require(maxDepth >= 0 && maxEntries > 0 && maxImages > 0)
    }

    fun scan(root: FolderDocument, supportsZip: Boolean, checkCancelled: () -> Unit = {}): FolderImageScan {
        require(root.isDirectory)
        val images = mutableListOf<FolderImage>()
        val visited = mutableSetOf<String>()
        val imageUris = mutableSetOf<String>()
        var visitedEntries = 0
        var limited = false

        fun visit(directory: FolderDocument, path: String, depth: Int) {
            checkCancelled()
            if (!visited.add(directory.uriString)) return
            for (document in directory.children()) {
                checkCancelled()
                if (visitedEntries >= maxEntries || images.size >= maxImages) {
                    limited = true
                    return
                }
                visitedEntries++
                val name = document.name ?: continue
                val relativePath = if (path.isEmpty()) name else "$path/$name"
                if (document.isDirectory) {
                    if (name.equals("workspace_dsuhelper", ignoreCase = true)) continue
                    if (depth >= maxDepth) {
                        limited = true
                    } else {
                        visit(document, relativePath, depth + 1)
                    }
                } else if (document.isFile) {
                    val format = folderImageFormat(name, supportsZip) ?: continue
                    if (imageUris.add(document.uriString)) {
                        images.add(FolderImage(document.uriString, name, relativePath, document.size, format))
                    }
                }
            }
        }
        visit(root, "", 0)
        return FolderImageScan(
            folderName = root.name.orEmpty(),
            images = images.sortedWith(compareBy({ it.filename.lowercase(Locale.ROOT) }, { it.relativePath.lowercase(Locale.ROOT) })),
            limited = limited,
        )
    }
}
