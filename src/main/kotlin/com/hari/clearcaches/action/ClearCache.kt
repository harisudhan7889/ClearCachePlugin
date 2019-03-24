package com.hari.clearcaches.action

import com.hari.clearcaches.ui.ClearCacheDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author Hari Hara Sudhan.N
 */
class ClearCache: AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val dialog = ClearCacheDialog(event.project!!)
        dialog.show()
    }

}