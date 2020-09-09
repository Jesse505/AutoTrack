package com.jesse.autotrack.plugin

import com.android.build.gradle.AppExtension
import com.jesse.autotrack.Log
import com.jesse.autotrack.extension.ReplaceExtension
import com.jesse.autotrack.transforms.ReplaceTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReplacePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        Log.i("zyf", "-----ReplacePlugin apply-----")
        ReplaceExtension replaceExtension = project.extensions.create('replace', ReplaceExtension)

        //注册
        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new ReplaceTransform(replaceExtension))
    }
}