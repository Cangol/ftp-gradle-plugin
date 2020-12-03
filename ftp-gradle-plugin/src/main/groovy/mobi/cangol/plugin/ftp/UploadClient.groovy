package mobi.cangol.plugin.ftp

import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileOutputStream
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class UploadClient {
    private static final Log log = LogFactory.getLog(UploadClient.class)
    private UploadPluginExtension extension
    private static UploadClient smbClient

    static UploadClient init(UploadPluginExtension extension) {
        if (smbClient == null) {
            smbClient = new UploadClient(extension)
        }
        return smbClient
    }

    UploadClient(UploadPluginExtension extension) {
        this.extension = extension
    }

    String upload(String destDir, String destFileName, String srcFilePath) {
        File srcFile=new File(srcFilePath)
        String filePath=extension.url+destDir+"/" + destFileName
        NtlmPasswordAuthentication authentication=new NtlmPasswordAuthentication(extension.domain,extension.username,extension.password)
        SmbFile destFile = new SmbFile("smb:"+ filePath, authentication)
        SmbFile dir=new SmbFile("smb:" + filePath.substring(0,filePath.lastIndexOf("/")),authentication)
        BufferedInputStream bin=null
        BufferedOutputStream out=null
        log.warn("dir="+dir.path)
        log.warn("destFile="+destFile.path)
        try{
            if (!dir.exists())
                dir.mkdirs()
            if (destFile.exists())
                destFile.delete()
            else
                destFile.createNewFile()
            bin = new BufferedInputStream(new FileInputStream(srcFile))
            out = new BufferedOutputStream(new SmbFileOutputStream(destFile))
            byte[] bytes = new byte[4096]
            int length
            while ((length=bin.read(bytes)) != -1) {
                out.write(bytes,0,length)
            }
            out.flush()
        }catch(Exception e){
            e.printStackTrace()
        }finally{
            if(out!=null){
                out.close()
            }
            if(bin!=null){
                bin.close()
            }
        }
        return filePath
    }

}
