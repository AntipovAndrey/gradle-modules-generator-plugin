package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.ModuleConfig
import com.github.antipovandrey.generator.model.ModuleTemplate
import com.github.antipovandrey.generator.model.Resources
import java.io.File
import java.nio.file.Files

object ModuleTemplateWriter {

    fun writeModuleTemplates(templates: List<ModuleTemplate>, config: ModuleConfig) {
        templates.forEach { writeModuleTemplate(it, config) }
    }

    private fun writeModuleTemplate(template: ModuleTemplate, config: ModuleConfig) {
        val moduleResolvedName = replaceTemplate(template.name, config)

        val moduleDirectory = File(config.baseModulesPath)
            .resolve(moduleResolvedName)
            .also { Files.createDirectories(it.toPath()) }

        writeStaticFiles(moduleDirectory, template.staticResources)
    }

    private fun writeStaticFiles(moduleDirectory: File, staticResources: Resources) {
        staticResources.files.forEach { file ->
            val fileRelativeToResources = file.relativeTo(staticResources.baseDirectory)
            file.copyTo(moduleDirectory.resolve(fileRelativeToResources), overwrite = true)
        }
    }

    private fun replaceTemplate(original: String, config: ModuleConfig): String {
        return original.replace("\${module.baseName}", config.baseName)
    }
}
