package com.github.antipovandrey.generator

import com.github.antipovandrey.generator.config.ConfigReader.readConfig
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbAware

class GenerateModuleAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val basePath = project.basePath ?: return

        val moduleTemplates = readConfig(basePath)

        WriteCommandAction.runWriteCommandAction(project) {

        }
    }
}
