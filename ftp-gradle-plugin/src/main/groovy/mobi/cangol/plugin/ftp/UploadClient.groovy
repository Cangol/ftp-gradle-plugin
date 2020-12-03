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
            ftpClient.connect(extension.getHost(),Integer.parseInt(extension.getPort()))
            ftpClient.login(extension.getUsername(), extension.getPassword())
            ftpClient.setBufferSize(4096);
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)

            fileInputStream=new FileInputStream(file)

            String[] arr =  filePath.split("/");
            for(String s : arr){
                if(s.length()==0||s.toLowerCase().endsWith(".apk")||s.toLowerCase().endsWith(".ipa"))continue;
                String dir = new String(s.getBytes(),ftpClient.getControlEncoding());
                if(ftpClient.changeWorkingDirectory(dir)){
                    log.warn("changeWorkingDirectory success ===> " + s)
                    continue;
                }else{
                    log.warn("changeWorkingDirectory fail ===> " + s)
                }
                if(!ftpClient.makeDirectory(dir)){
                    log.warn("makeDirectory fail ===> " + s)
                    break;
                }else{
                    log.warn("makeDirectory success ===> " + s)
                    ftpClient.changeWorkingDirectory(dir)
                }
            }

            boolean result=ftpClient.storeFile(file.getName(),fileInputStream)
            log.warn("storeFile ===> " + result)
        }catch(Exception e){
            log.error("upload"+e.getMessage())
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
