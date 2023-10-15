package com.hoffi.compose.showcase.glasslayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

interface VisibleWithShowHideIfc {
    var visible: Boolean
    fun show()
    fun hide()
    fun isVisible() : Boolean
}


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
        val paneComposables = mutableStateListOf<ComposableClass>()
        val allPaneComposableStates = mutableListOf<VisibleWithShowHideIfc>()
        val panePopups = mutableStateListOf<PopupClass>()
        val allPanePopupStates = mutableListOf<VisibleWithShowHideIfc>()
        /** when PanePopupCardinality is SinglePopup, hide all visible Popups of its GlassPane on showing a new Popup */
        fun cardinality() {
            when (panePopupCardinality) {
                PanePopupCardinality.SinglePopup -> allPanePopupStates.forEach { it.hide() }
                PanePopupCardinality.MultiplePopups -> {}
            }
        }

        @Composable
        open fun glassLayerContent() {
            for (paneComposable in paneComposables) {
                paneComposable.ComposableContent()
            }
        }
        @Composable
        open fun glassLayerPopupContent() {
            for (popup in panePopups) {
                popup.PopupContent()
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
