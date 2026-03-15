package com.limelight.ui.compose

import android.app.Activity
import android.view.View
import androidx.compose.ui.platform.ComposeView
import com.limelight.PcView
import com.limelight.grid.PcGridAdapter

object ComposeBridge {
    @JvmStatic
    fun createPcViewCompose(
        activity: PcView, 
        adapter: PcGridAdapter, 
        onSettingsClick: Runnable,
        onHelpClick: Runnable,
        onAddAppClick: Runnable
    ): View {
        return ComposeView(activity).apply {
            setContent {
                PcViewScreen(
                    adapter = adapter,
                    onSettingsClick = { onSettingsClick.run() },
                    onHelpClick = { onHelpClick.run() },
                    onAddAppClick = { onAddAppClick.run() }
                )
            }
        }
    }
}
