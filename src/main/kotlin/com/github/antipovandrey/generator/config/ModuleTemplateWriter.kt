package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.ModuleConfig
import com.github.antipovandrey.generator.model.ModuleTemplate
import com.github.antipovandrey.generator.model.Resources
import java.io.File
import java.nio.file.Files

object ModuleTemplateWriter {

    fun writeModuleTemplates(templates: List<ModuleTemplate>, moduleBaseName: String, basePath: String) {
        templates.forEach { writeModuleTemplate(it, moduleBaseName, basePath) }
    }

    private fun writeModuleTemplate(template: ModuleTemplate, moduleBaseName: String, basePath: String) {
        val moduleConfig = ModuleConfig(moduleBaseName)
        val resolvedConfig = ModuleTemplateReader.readResolvedConfig(
            template.configFile,
            mapOf(Settings.moduleTemplateKey to moduleConfig)
        )

        val moduleResolvedName = resolvedConfig.name

        val projectRoot = File(basePath)

        val moduleDirectory = projectRoot
            .resolve(moduleResolvedName)
            .also { Files.createDirectories(it.toPath()) }

        writeDirectories(resolvedConfig.directories, moduleDirectory)
        writeStaticFiles(template, moduleDirectory)
        writeTemplateFiles(template, moduleDirectory, moduleConfig)
        writeGradleSettings(resolvedConfig.name, projectRoot)
    }

    private fun writeDirectories(directoriesToCreate: List<String>, root: File) {
        directoriesToCreate.map { root.resolve(it) }
            .forEach { Files.createDirectories(it.toPath()) }
    }

    private fun writeStaticFiles(template: ModuleTemplate, moduleDirectory: File) {
        template.staticResources.files.forEach { file ->
            val target = getRelativeToModule(file, template.staticResources, moduleDirectory)
            file.copyTo(target, overwrite = true)
        }
    }

    private fun writeTemplateFiles(template: ModuleTemplate, moduleDirectory: File, config: ModuleConfig) {
        val templateProperties = mapOf(Settings.moduleTemplateKey to config)
        val templateResources = template.templateResources
        templateResources.files
            .filterNot { it.isDirectory }
            .forEach { templateFile ->
                val targetFile = getRelativeToModule(templateFile, template.templateResources, moduleDirectory)
                targetFile.parentFile.mkdirs()
                targetFile.createNewFile()
                TemplateResolver.resolveTemplate(templateFile, targetFile, templateProperties)
            }
    }

    private fun writeGradleSettings(moduleName: String, projectRoot: File) {
        val gradleFile = projectRoot.resolve(Settings.gradleSettings)
        val gradleFileKts = projectRoot.resolve(Settings.gradleSettingsKts)
        if (gradleFile.exists()) {
            gradleFile.appendText("\ninclude ':$moduleName'")
        } else if (gradleFileKts.exists()) {
            gradleFileKts.appendText("\ninclude(\"$moduleName\"")
        }
    }

    private fun getRelativeToModule(originalFile: File, resources: Resources, moduleDirectory: File): File {
        val fileRelativeToResources = originalFile.relativeTo(resources.baseDirectory)
        return moduleDirectory.resolve(fileRelativeToResources)
    }
}
