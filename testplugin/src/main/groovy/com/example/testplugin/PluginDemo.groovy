package com.example.testplugin


import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginDemo implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("========== this is a log just from PluginDemo ==========")
        AppExtension extension = project.extensions.getByType(AppExtension.class)
        TestTransfrom transfrom = new TestTransfrom()
        extension.registerTransform(transfrom)
    }
}