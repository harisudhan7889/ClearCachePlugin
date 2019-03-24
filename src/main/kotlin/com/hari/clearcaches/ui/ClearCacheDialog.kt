package com.hari.clearcaches.ui

import com.hari.clearcaches.Callback
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.deleteFile
import org.apache.commons.lang.SystemUtils
import java.util.*
import javax.swing.JComponent

/**
 * @author Hari Hara Sudhan
 */
class ClearCacheDialog constructor(private val project: Project): DialogWrapper(true),
    Callback {

    private val panel = ClearCachePanel(this)
    private var isDelete = false

    override fun createCenterPanel(): JComponent? = panel

    init {
        title = "Clear your project caches"
        init()
    }

    override fun doOKAction() {
        if(!isDelete) {
            if(panel.getPackagePrefix().isEmpty()) {
                panel.setTextMessage("Please specify the package prefix name")
            } else {
                getCachePackages()
            }
        } else {
            val packages = panel.getSelectedPackages()
            deleteCaches(packages)
            isDelete = false
            super.doOKAction()
        }
    }

    override fun doCancelAction() {
        if(isDelete) {
            panel.clearResult()
            setCancelButtonText("Cancel")
            setOKButtonText("Ok")
            isOKActionEnabled = true
            isDelete = false
        } else {
            super.doCancelAction()
        }
    }

    override fun isAnyCheckboxSelected(isSelected: Boolean) {
        isOKActionEnabled = isSelected
    }

    private fun getCachePackages() {
        val homeDirectory = LocalFileSystem.getInstance().findFileByIoFile(SystemUtils.getUserHome())
        homeDirectory
            ?.findChild(".gradle")
            ?.findChild("caches")
            ?.findChild("modules-2")
            ?.let {
                    val virtualFiles = it.children
                    val length = virtualFiles.size
                    val arrayList = ArrayList<String>()
                    for (i in 0 until length) {
                        if (virtualFiles[i].isDirectory) {
                            val innerVirtualFiles = virtualFiles[i].children
                            for (j in innerVirtualFiles.indices) {
                                if (innerVirtualFiles[j].name.startsWith(panel.getPackagePrefix())) {
                                    if (!arrayList.contains(innerVirtualFiles[j].name)) {
                                        arrayList.add(innerVirtualFiles[j].name)
                                    }
                                }
                            }
                        }
                    }
                    if (arrayList.size > 0) {
                        panel.setCheckboxes(arrayList)
                        isOKActionEnabled = false
                        setOKButtonText("Delete")
                        setCancelButtonText("Clear")
                        isDelete = true
                    } else {
                        panel.setTextMessage("Please specify the correct package prefix")
                    }
            }
    }

    /**
     * Delete the selected cache packages.
     */
    private fun deleteCaches(packages: ArrayList<String>) {
        val homeDirectory = LocalFileSystem.getInstance().findFileByIoFile(SystemUtils.getUserHome())
        homeDirectory
            ?.findChild(".gradle")
            ?.findChild("caches")
            ?.findChild("modules-2")
            ?.let {
                val virtualFiles = it.children
                val length = virtualFiles.size
                for (i in 0 until length) {
                    if (virtualFiles[i].isDirectory) {
                        val innerVirtualFiles = virtualFiles[i].children
                        for (j in innerVirtualFiles.indices) {
                            if(innerVirtualFiles[j].name == "descriptors") {
                                val nestedInnerChildren = innerVirtualFiles[j].children
                                for (k in nestedInnerChildren.indices) {
                                    for (nestedSinglePackage in packages) {
                                        if (nestedInnerChildren[k].name == nestedSinglePackage) {
                                            deleteFile(nestedInnerChildren[k])
                                        }
                                    }
                                }
                            } else {
                                for (singlePackage in packages) {
                                    if (innerVirtualFiles[j].name == singlePackage) {
                                        deleteFile(innerVirtualFiles[j])
                                    }
                                }
                            }
                        }
                    }
                }
                showNotification()
            }
    }


    /**
     * Displays the notification after the deletion of caches.
     */
    private fun showNotification() {
        val notification = NotificationGroup("Clear Cache", NotificationDisplayType.BALLOON, true)
        notification.createNotification(
            "Clear Cache",
            "Selected cached packages are deleted",
            NotificationType.INFORMATION,
            null
        ).notify(project)
    }

}