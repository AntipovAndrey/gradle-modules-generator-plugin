package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.ModuleConfig
import com.github.antipovandrey.generator.model.ModuleTemplate

object ConfigWriter {


    fun writeModuleTemplates(templates: List<ModuleTemplate>, config: ModuleConfig) {

        templates.forEach { moduleTemplate ->
            val moduleName = replaceTemplate(moduleTemplate.name, config)
        }
    }

    private fun replaceTemplate(original: String, config: ModuleConfig): String {
        return original.replace("\${module.baseName}", config.baseName)
    }
}