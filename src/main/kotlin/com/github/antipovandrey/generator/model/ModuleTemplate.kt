package com.github.antipovandrey.generator.model

import java.io.File

data class ModuleTemplate(
    val name: String,
    val staticFiles: List<File>,
    val templateFiles: List<File>
)