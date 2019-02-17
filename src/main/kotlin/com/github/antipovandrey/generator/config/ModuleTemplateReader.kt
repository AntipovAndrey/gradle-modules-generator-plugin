package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.model.ModuleTemplate
import com.github.antipovandrey.generator.model.Resources
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileFilter
import java.io.StringReader

object ModuleTemplateReader {

    private val yaml = Yaml()

    fun readModuleTemplates(projectPath: String): List<ModuleTemplate> {
        val templatesPath = File(projectPath)
            .resolve(Settings.templateFolderName)
        if (!templatesPath.exists()) {
            throw InvalidConfigException("${Settings.templateFolderName} not found in the project")
        }
        return templatesPath
            .listFiles(FileFilter { it.isDirectory })
            .map { readModule(it) }
    }

    fun readResolvedConfig(config: File, props: Map<String, Any>): Config {
        val resolvedTemplateString = TemplateResolver.resolveTemplate(config, props)
        return yaml.loadAs<Config>(StringReader(resolvedTemplateString), Config::class.java)
    }

    private fun readModule(moduleDirectory: File): ModuleTemplate {
        val configFile = moduleDirectory.resolve(Settings.configFileName)
        val staticResources = getResources(moduleDirectory, Settings.staticFilesName)
        val templateResources = getResources(moduleDirectory, Settings.templateFilesName)
        return ModuleTemplate(configFile, staticResources, templateResources)
    }

    private fun getResources(moduleDirectory: File, resourcesName: String): Resources {
        val resourcesDirectory = moduleDirectory.resolve(resourcesName)
        return Resources(findFiles(resourcesDirectory), resourcesDirectory)
    }

    private fun findFiles(directory: File): List<File> {
        return directory.walk().toList().filterNot { it == directory }
    }
}