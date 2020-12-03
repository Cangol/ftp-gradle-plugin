package mobi.cangol.plugin.ftp

import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UploadTask extends DefaultTask {
    UploadPluginExtension extension
    UploadClient client
    ApplicationVariant variant

    @TaskAction
    upload() {
        def log = project.logger
        if (client == null) {
            client = UploadClient.init(extension)
        }

        def apkOutput = variant.outputs.find { variantOutput -> variantOutput instanceof ApkVariantOutput }

        String apkPath = apkOutput.outputFile.getAbsolutePath()
        log.warn("apkPath ===> " + apkPath)

        def destDirPath = extension.getProperty(variant.buildType.name + "Dir");
        if (destDirPath == null) {
            destDirPath = ""
        }
        log.warn("destDirPath ===> " + destDirPath)

        def path = client.upload(destDirPath, apkOutput.outputFile.name, apkPath)
        log.warn("upload ===> " + path)

    }

}
