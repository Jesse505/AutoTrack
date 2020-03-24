package com.jesse.autotrack.plugin

import com.android.build.gradle.AppExtension
import com.jesse.autotrack.transforms.TestTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoTrackPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println("-----------AutoTrackPlugin apply-------------")
        //获取埋点的开关
        boolean enableAutoTrack = false
        Properties properties = new Properties()
        if (project.rootProject.file('gradle.properties').exists()) {
            properties.load(project.rootProject.file('gradle.properties').newDataInputStream())
            enableAutoTrack = Boolean.parseBoolean(properties.getProperty("autoTrack.enable", "true"))
        }

        //注册
        if (enableAutoTrack) {
            println("-----------AutoTrackPlugin enable-------------")
            AppExtension appExtension = project.extensions.findByType(AppExtension.class)
            appExtension.registerTransform(new TestTransform())
        } else {
            println("-----------AutoTrackPlugin disable-------------")
        }
    }
}