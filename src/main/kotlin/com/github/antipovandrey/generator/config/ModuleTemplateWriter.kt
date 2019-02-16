package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.ModuleConfig
import com.github.antipovandrey.generator.model.ModuleTemplate
import com.github.antipovandrey.generator.model.Resources
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

object ModuleTemplateWriter {

    private val velocityEngine = VelocityEngine()

    init {
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file")
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "/")
        velocityEngine.init()
    }

    fun writeModuleTemplates(templates: List<ModuleTemplate>, moduleBaseName: String, basePath: String) {
        templates.forEach { writeModuleTemplate(it, moduleBaseName, basePath) }
    }

    private fun writeModuleTemplate(template: ModuleTemplate, moduleBaseName: String, basePath: String) {
        val moduleResolvedName = replaceTemplate(template.name, moduleBaseName)

        val moduleDirectory = File(basePath)
            .resolve(moduleResolvedName)
            .also { Files.createDirectories(it.toPath()) }

        val config = ModuleConfig(moduleBaseName, moduleDirectory)

        writeStaticFiles(template, config)
        writeTemplateFiles(template, config)
    }

    private fun writeStaticFiles(template: ModuleTemplate, config: ModuleConfig) {
        template.staticResources.files.forEach { file ->
            val target = getRelativeToModule(file, template.staticResources, config.moduleDirectory)
            file.copyTo(target, overwrite = true)
        }
    }

    private fun writeTemplateFiles(template: ModuleTemplate, config: ModuleConfig) {
        val templateProperties = mapOf(Settings.moduleTemplateKey to config)
        val templateResources = template.templateResources
        templateResources.files
            .filterNot { it.isDirectory }
            .forEach { templateFile ->
                val targetFile = getRelativeToModule(templateFile, template.templateResources, config.moduleDirectory)
                targetFile.parentFile.mkdirs()
                targetFile.createNewFile()
                resolveTemplate(templateFile, targetFile, templateProperties)
            }
    }

    // todo: move to an external class
    private fun resolveTemplate(source: File, targetFile: File, props: Map<String, Any>) {
        val velocityTemplate = velocityEngine.getTemplate(source.absolutePath)
        val writer = FileWriter(targetFile)
        writer.use {
            velocityTemplate.merge(VelocityContext(props), writer)
        }
    }

    private fun getRelativeToModule(originalFile: File, resources: Resources, moduleDirectory: File): File {
        val fileRelativeToResources = originalFile.relativeTo(resources.baseDirectory)
        return moduleDirectory.resolve(fileRelativeToResources)
    }

    // todo: resolve config file as a template before it gets here
    private fun replaceTemplate(original: String, moduleBaseName: String): String {
        return original.replace("\${${Settings.moduleTemplateKey}.baseName}", moduleBaseName)
    }
}
