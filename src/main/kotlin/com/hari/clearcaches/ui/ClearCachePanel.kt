package com.hari.clearcaches.ui

import com.hari.clearcaches.Callback
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

/**
 * @author Hari Hara Sudhan.N
 */
class ClearCachePanel(callback: Callback) : JPanel() {
    private var contentPanel: JPanel?=null
    private var jScrollPane: JBScrollPane?=null
    private var jLabel: JLabel?=null
    private val packageNameSpaceText = JTextField()
    private var checkedItemCount = 0
    private val callback = callback

    init {
        initComponents()
    }

    override fun getPreferredSize() = Dimension(300, 200)

    /**
     * UI initialization.
     */
    private fun initComponents() {
        layout = null

        val packageNameSpace = JLabel("Package Prefix")
        packageNameSpace.setBounds(15, 25, 120, 20)
        add(packageNameSpace)

        packageNameSpaceText.setBounds(125, 20, 150, 30)
        add(packageNameSpaceText)
    }

    /**
     * Gets the input package name given by the user.
     */
    fun getPackagePrefix(): String {
        return packageNameSpaceText.text
    }

    /**
     * Clears the text and checkboxes.
     */
    fun clearResult(){
        packageNameSpaceText.text = ""
        jScrollPane?.isVisible = false
    }

    /**
     * Sets and displays the error message.
     */
    fun setTextMessage(message: String) {
        jScrollPane?.let {
            if(it.isVisible) {
                it.isVisible = false
            }
        }
        jLabel?.let {
            if(it.isVisible) {
                it.isVisible = false
            }
        }
        jLabel = JLabel(message)
        jLabel?.setBounds(50, 110, 250, 20)
        add(jLabel)
        revalidate()
        repaint()
    }

    /**
     * Sets the retrieved result package names in checkbox.
     */
    fun setCheckboxes(cachesArray: ArrayList<String>) {
        jLabel?.let {
            if(it.isVisible) {
                it.isVisible = false
            }
        }
        contentPanel = JPanel(GridLayout(0, 1))
        contentPanel?.autoscrolls = true
        val border = BorderFactory.createTitledBorder("Cached Packages")
         cachesArray.forEach { c ->
             var checkbox = JCheckBox(c)
             checkbox.addItemListener{
                     if(checkbox.isSelected) {
                         checkedItemCount++
                     } else if(checkedItemCount >0) {
                         checkedItemCount--
                     }

                 if (checkedItemCount > 0) {
                     callback.isAnyCheckboxSelected(true)
                 } else {
                     callback.isAnyCheckboxSelected(false)
                 }
             }
             contentPanel?.add(checkbox)
         }
        contentPanel?.setSize(250, 500)
        jScrollPane = JBScrollPane(contentPanel)
        jScrollPane?.setBounds(25, 55, 250, 130)
        jScrollPane?.border = border
        add(jScrollPane)
        revalidate()
        repaint()
    }

    /**
     * Gets the selected packages for deletion.
     */
    fun getSelectedPackages(): ArrayList<String> {
        val packages = ArrayList<String>()
        contentPanel?.let {
            contentPanel?.components?.forEach { c ->
                c as JCheckBox
            if(c.isSelected) {
                packages.add(c.text)
            }}
        }
        return packages
    }
}