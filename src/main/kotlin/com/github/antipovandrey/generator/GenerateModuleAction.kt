package com.github.antipovandrey.generator

import com.github.antipovandrey.generator.config.ModuleTemplateReader
import com.github.antipovandrey.generator.config.ModuleTemplateWriter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.showInputDialog
import com.intellij.openapi.ui.Messages.showMessageDialog

class GenerateModuleAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val basePath = project.basePath ?: return

        val moduleBaseName = askUserForModuleBaseName(project) ?: return

        if (moduleBaseName.isBlank()) {
            showMessageDialog(project, "You have to input module's base name", "No name specified", null)
            return
        }

        val moduleTemplates = ModuleTemplateReader.readModuleTemplates(basePath)
        WriteCommandAction.runWriteCommandAction(project) {
            ModuleTemplateWriter.writeModuleTemplates(moduleTemplates, moduleBaseName, basePath)
        }
    }

    private fun askUserForModuleBaseName(project: Project): String? {
        val inputName: String? = showInputDialog(project, "Enter new module base name", "New Module", null)
        return inputName?.trim()
    }
}
