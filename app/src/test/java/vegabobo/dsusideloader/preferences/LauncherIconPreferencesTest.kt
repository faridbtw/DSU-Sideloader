package vegabobo.dsusideloader.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.Element

class LauncherIconPreferencesTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun missingAndUnknownIconsUseMaterial() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { File(temporaryFolder.root, "icons.preferences_pb") })
        try {
            val icons = LauncherIconPreferences(store)
            assertEquals(LauncherIcon.MATERIAL, icons.selectedIcon.first())
            store.edit { it[stringPreferencesKey("launcher_icon")] = "future-icon" }
            assertEquals(LauncherIcon.MATERIAL, icons.selectedIcon.first())
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun stockBlueSurvivesReopeningAndThemeReset() = runBlocking {
        val file = File(temporaryFolder.root, "saved-icons.preferences_pb")
        val firstScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val firstStore = PreferenceDataStoreFactory.create(scope = firstScope, produceFile = { file })
        try {
            LauncherIconPreferences(firstStore).setIcon(LauncherIcon.STOCK)
        } finally {
            firstScope.coroutineContext[Job]!!.cancelAndJoin()
        }
        val secondScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val secondStore = PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file })
        try {
            val icons = LauncherIconPreferences(secondStore)
            assertEquals(LauncherIcon.STOCK, icons.selectedIcon.first())
            AppearancePreferences(secondStore).reset()
            assertEquals(LauncherIcon.STOCK, icons.selectedIcon.first())
        } finally {
            secondScope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun everyChoiceHasOneLaunchableAliasAndTheTargetActivityStaysEnabled() {
        val manifest = listOf(File("src/main/AndroidManifest.xml"), File("app/src/main/AndroidManifest.xml")).first { it.isFile }
        val document = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
            .newDocumentBuilder().parse(manifest)
        val android = "http://schemas.android.com/apk/res/android"
        val nodes = document.getElementsByTagName("activity-alias")
        val aliases = (0 until nodes.length).map { nodes.item(it) as Element }
        assertEquals(LauncherIcon.entries.size, aliases.size)
        assertEquals(LauncherIcon.entries.map { ".${it.componentSuffix}" }.toSet(), aliases.map { it.getAttributeNS(android, "name") }.toSet())
        assertEquals(1, aliases.count { it.getAttributeNS(android, "enabled") == "true" })
        assertEquals(".LauncherMaterial", aliases.single { it.getAttributeNS(android, "enabled") == "true" }.getAttributeNS(android, "name"))
        aliases.forEach { alias ->
            assertEquals(".MainActivity", alias.getAttributeNS(android, "targetActivity"))
            assertEquals("true", alias.getAttributeNS(android, "exported"))
            assertEquals("android.intent.action.MAIN", (alias.getElementsByTagName("action").item(0) as Element).getAttributeNS(android, "name"))
            assertEquals("android.intent.category.LAUNCHER", (alias.getElementsByTagName("category").item(0) as Element).getAttributeNS(android, "name"))
            val icon = alias.getAttributeNS(android, "icon").substringAfter("@mipmap/")
            assertTrue(File(manifest.parentFile, "res/mipmap-anydpi/$icon.xml").isFile)
        }
        val activities = document.getElementsByTagName("activity")
        val target = (0 until activities.length).map { activities.item(it) as Element }
            .single { it.getAttributeNS(android, "name") == ".MainActivity" }
        assertFalse(target.getAttributeNS(android, "enabled") == "false")
        assertEquals(0, target.getElementsByTagName("intent-filter").length)
    }

    @Test
    fun allIconIdentitiesAreStableAndDistinct() = runBlocking {
        assertEquals(13, LauncherIcon.entries.size)
        assertEquals(13, LauncherIcon.entries.map { it.preferenceValue }.distinct().size)
        assertEquals(13, LauncherIcon.entries.map { it.componentClass("vegabobo.dsusideloader") }.distinct().size)
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { File(temporaryFolder.root, "all-icons.preferences_pb") })
        try {
            val icons = LauncherIconPreferences(store)
            LauncherIcon.entries.forEach { icon ->
                icons.setIcon(icon)
                assertEquals(icon, icons.selectedIcon.first())
            }
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }
}
