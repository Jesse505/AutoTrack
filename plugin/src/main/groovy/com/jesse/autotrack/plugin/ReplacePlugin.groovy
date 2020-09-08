package com.jesse.autotrack.plugin

import com.android.build.gradle.AppExtension
import com.jesse.autotrack.transforms.AutoTrackTransform
import com.jesse.autotrack.transforms.ReplaceTransform
import com.jesse.autotrack.transforms.TestTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReplacePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println("-----------ReplacePlugin apply-------------")

        //注册
        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new ReplaceTransform())
    }
}