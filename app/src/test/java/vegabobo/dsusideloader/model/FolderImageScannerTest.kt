package vegabobo.dsusideloader.model

import java.util.concurrent.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderImageScannerTest {
    private class Document(
        override val name: String,
        override val isDirectory: Boolean = false,
        override val uriString: String = "test:///$name",
        val entries: MutableList<FolderDocument> = mutableListOf(),
    ) : FolderDocument {
        override val isFile get() = !isDirectory
        override val size = 4096L
        override fun children(): List<FolderDocument> = entries
    }

    private fun folder(name: String, vararg children: FolderDocument) =
        Document(name, isDirectory = true, entries = children.toMutableList())

    @Test
    fun discoversSupportedImagesInSubfoldersAndSortsThem() {
        val root = folder(
            "GSIs",
            Document("Pixel.zip"),
            folder("Downloads", Document("Lineage.img.xz")),
            Document("Alpha.IMG"),
            Document("notes.txt"),
        )
        val scan = FolderImageScanner().scan(root, supportsZip = true)
        assertEquals("GSIs", scan.folderName)
        assertEquals(listOf("Alpha.IMG", "Lineage.img.xz", "Pixel.zip"), scan.images.map { it.filename })
        assertEquals("Downloads/Lineage.img.xz", scan.images[1].relativePath)
        assertEquals(4096L, scan.images[1].size)
        assertFalse(scan.limited)
    }

    @Test
    fun ignoresTemporaryWorkspaceAndNonSystemPartitions() {
        val root = folder(
            "GSIs",
            folder("workspace_dsuhelper", Document("prepared-system.img")),
            Document("userdata.img"),
            Document("userdata_gsi.img"),
            Document("vendor_boot_a.img"),
            Document("vbmeta_system_b.img"),
            Document("vendor.img.xz"),
            Document("BOOT.IMG.GZ"),
            Document("super.img"),
            Document("system.img"),
        )
        val scan = FolderImageScanner().scan(root, true)
        assertEquals(listOf("system.img"), scan.images.map { it.filename })
    }

    @Test
    fun zipPackagesAreExcludedOnAndroid10() {
        val root = folder("GSIs", Document("system.zip"), Document("system.img.gz"))
        assertEquals(listOf("system.img.gz"), FolderImageScanner().scan(root, false).images.map { it.filename })
        assertEquals(2, FolderImageScanner().scan(root, true).images.size)
    }

    @Test
    fun recognizesCompressedNamesAndUppercaseExtensions() {
        assertEquals(FolderImageFormat.GZIP, folderImageFormat("Android.IMG.GZ", true))
        assertEquals(FolderImageFormat.GZIP, folderImageFormat("Android.GZIP", true))
        assertEquals(FolderImageFormat.XZ, folderImageFormat("Android.XZ", true))
        assertEquals(FolderImageFormat.DSU_ZIP, folderImageFormat("Android.ZIP", true))
        assertNull(folderImageFormat("photo.png", true))
        assertNull(folderImageFormat("Android.zip", false))
    }

    @Test
    fun depthLimitIsReportedWithoutHidingFilesInTheRoot() {
        val root = folder("GSIs", folder("Deep", Document("nested.img")), Document("root.img"))
        val scan = FolderImageScanner(maxDepth = 0).scan(root, true)
        assertEquals(listOf("root.img"), scan.images.map { it.filename })
        assertTrue(scan.limited)
    }

    @Test
    fun entryAndImageLimitsBoundLargeFolders() {
        val root = folder("GSIs", Document("a.img"), Document("b.img"), Document("c.img"))
        val entries = FolderImageScanner(maxEntries = 1).scan(root, true)
        assertEquals(1, entries.images.size)
        assertTrue(entries.limited)
        val images = FolderImageScanner(maxImages = 2).scan(root, true)
        assertEquals(2, images.images.size)
        assertTrue(images.limited)
    }

    @Test
    fun duplicateDocumentsAndDirectoryCyclesDoNotRepeatResults() {
        val image = Document("system.img")
        val root = folder("GSIs", image, image)
        root.entries.add(root)
        val scan = FolderImageScanner().scan(root, true)
        assertEquals(1, scan.images.size)
        assertFalse(scan.limited)
    }

    @Test(expected = CancellationException::class)
    fun cancelledScansStopBeforeTraversingTheProvider() {
        FolderImageScanner().scan(folder("GSIs", Document("system.img")), true) {
            throw CancellationException("Folder changed")
        }
    }
}
