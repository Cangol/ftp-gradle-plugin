package mobi.cangol.plugin.ftp

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.commons.net.ftp.FTPClient

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
        File file=new File(srcFilePath)
        String filePath=destDir+"/" + destFileName

        FTPClient ftpClient=null
        FileInputStream fileInputStream=null
        try{

            ftpClient=new FTPClient()
            ftpClient.connect(extension.getHost(),extension.getPort())
            ftpClient.login(extension.getUsername(), extension.getPassword())
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)

            fileInputStream=new FileInputStream(file)
            ftpClient.storeFile(filePath,fileInputStream)

        }catch(Exception e){
            e.printStackTrace()
        }finally{
            if(fileInputStream!=null){
                fileInputStream.close()
            }
            if(ftpClient!=null){
                ftpClient.disconnect();
            }
        }

        return filePath
    }

}
