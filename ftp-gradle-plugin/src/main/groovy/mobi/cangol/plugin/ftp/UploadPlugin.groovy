package mobi.cangol.plugin.ftp

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class UploadPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def log = project.logger

        def hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'com.android.application' plugin is required.")
        }
        def extension = project.extensions.create('smbfile', UploadPluginExtension)

        project.android.applicationVariants.all { variant ->
            if (extension == null) {
                log.error("Please config your smbfile(smb,username,password) in your build.gradle.")
                return
            }
            if (extension.url==null&&(extension.username==null&&extension.password==null)) {
                log.error("Please config your smbfile(smb,username,password) in your build.gradle.")
                return
            }

            def buildTypeName = variant.buildType.name.capitalize()

            def productFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
            if (productFlavorNames.isEmpty()) {
                productFlavorNames = [""]
            }
            def productFlavorName = productFlavorNames.join('')
            def variationName = "${productFlavorName}${buildTypeName}"
            def uploadApkTaskName = "smbfileUpload${variationName}"
            def assembleTask = extension.dependsOn != null ? "${extension.dependsOn}${variationName}" : variant.assemble
            log.info("uploadApkTaskName == " + uploadApkTaskName)
            def uploadApkTask = project.tasks.create(uploadApkTaskName, UploadTask)
            uploadApkTask.extension = extension
            uploadApkTask.variant = variant
            uploadApkTask.description = "Uploads the APK for the ${variationName} build"
            uploadApkTask.group = "smbfile"
            uploadApkTask.dependsOn assembleTask
        }
    }
}
