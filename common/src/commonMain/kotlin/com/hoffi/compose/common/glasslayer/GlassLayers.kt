package com.hoffi.compose.common.glasslayer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.hoffi.compose.common.layout.SheetPosition

interface VisibleWithShowHideIfc {
    var visible: Boolean
    fun show()
    fun hide()
    fun isVisible() : Boolean
}

@OptIn(ExperimentalMaterialApi::class)
abstract class AGlassLayerComposableClass(
    val id: Any,
    var rectSize: MutableState<RectSize>,
    val content: @Composable () -> Unit
) {
    //companion object { val NOCONTENT = GlassLayerNOCONTENT }
    @Composable abstract fun ComposableContent()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AGlassLayerComposableClass) return false
        if (id != other.id) return false
        return true
    }
    override fun hashCode() = id.hashCode()
}
//object GlassLayerNOCONTENT : AGlassLayerComposableClass("<NOCONTENT>", mutableStateOf(RectSize.Zero), com.hoffi.compose.common.NOCONTENT) { @Composable override fun ComposableContent() {} }

object GlassLayers {
    // =======================
    // GlassLayers' methods ...
    // =======================
    fun hideAll() {
        glassPaneLayers.forEach {
            it.allPanePopupStates.forEach {
                it.hide()
            }
        }
    }

    enum class PanePopupCardinality { SinglePopup, MultiplePopups }
    val editGlassPane = EditGlassPane(PanePopupCardinality.SinglePopup)
    val sheetGlassPane = SheetGlassPane(PanePopupCardinality.MultiplePopups)
    val navigationGlassPane = NavigationGlassPane(PanePopupCardinality.SinglePopup)
    val dialogGlassPane = DialogGlassPane(PanePopupCardinality.MultiplePopups)
    val hintGlassPane = HintGlassPane(PanePopupCardinality.MultiplePopups)
    val notificationGlassPane = NotificationGlassPane(PanePopupCardinality.MultiplePopups)
    val lockGlassPane = LockGlassPane(PanePopupCardinality.MultiplePopups)
    val fatalGlassPane = FatalGlassPane(PanePopupCardinality.MultiplePopups)

    val glassPaneLayers = listOf(
        editGlassPane,
        sheetGlassPane,
        navigationGlassPane,
        dialogGlassPane,
        hintGlassPane,
        notificationGlassPane,
        lockGlassPane,
        fatalGlassPane
    )

    /** As `fun AppWithGlassLayers(appContent: @Composable () -> Unit)` wraps the whole App Window in a Box,<br/>
     * we can draw stuff _on top of_ the main app content (like on `GlassPane`'s that lie over the App Window).<br/>
     * Each (ordered) `GlassPane` draws its stuff on top of the previous one, the main App being the lowest of all layers.
     *
     * Each GlassPane has its own popups, drawn on top of the stuff of its layer. */
    abstract class GlassPane(val panePopupCardinality: PanePopupCardinality) {
        protected val paneComposables = mutableStateListOf<AGlassLayerComposableClass>()
        val allPaneComposableStates = mutableListOf<VisibleWithShowHideIfc>()
        protected val panePopups = mutableStateListOf<PopupClass>()
        val allPanePopupStates = mutableListOf<VisibleWithShowHideIfc>()

        /** when PanePopupCardinality is SinglePopup, hide all visible Popups of its GlassPane on showing a new Popup */
        fun cardinality() {
            when (panePopupCardinality) {
                PanePopupCardinality.SinglePopup -> allPanePopupStates.forEach { it.hide() }
                PanePopupCardinality.MultiplePopups -> {}
            }
        }

        fun addComposable(glassLayerComposableClass: AGlassLayerComposableClass) = paneComposables.add(glassLayerComposableClass)
        fun removeComposable(glassLayerComposableClass: AGlassLayerComposableClass) = paneComposables.remove(glassLayerComposableClass)
        fun isVisible(id: Any) = paneComposables.any { it.id == id }
        fun addPopup(popupClass: PopupClass) = panePopups.add(popupClass)
        fun removePopup(popupClass: PopupClass) = panePopups.remove(popupClass)

        @Composable
        open fun glassLayerContent() {
            for (paneComposable in paneComposables) {
                paneComposable.ComposableContent()
            }
        }
        @Composable
        open fun glassLayerPopupContent() {
            for (popup in panePopups) {
                popup.ComposableContent()
            }
        }
    }
    /** GlassPane for Editing main content within a GlassPane component or popup */
    class EditGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {
        //private val editContextPopupState = AutoCompleteState(startItems = listOf<AutoCompleteEntity>()).also { allPanePopupStates.add(it) }
        //fun showEditContextPopup() { cardinality() ; editContextPopupState.show() }
        //fun hideEditContextPopup() { editContextPopupState.hide() }
        //fun isVisibleEditContextPopup() = editContextPopupState.isVisible()
    }
    class SheetGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
    class NavigationGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {
        //val quickSearchAutoCompleteState = remember { AutoCompleteState(startItems = listOf<Person>()) }.also { allPanePopupStates.add(it) }
        //fun showQuickSearchPopup() { cardinality() ; quickSearchAutoCompleteState.show() }
        //fun hideQuickSearchPopup() { quickSearchAutoCompleteState.hide() }
        //fun isVisibleQuickSearchPopup() = quickSearchAutoCompleteState.isVisible()
    }
    class DialogGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
    class HintGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
    class NotificationGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
    class LockGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
    class FatalGlassPane(popupCardinality: PanePopupCardinality) : GlassPane(popupCardinality) {}
}
