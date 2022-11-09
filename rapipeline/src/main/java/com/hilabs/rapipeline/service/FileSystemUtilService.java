package com.hilabs.rapipeline.service;

import com.hilabs.rapipeline.config.AppPropertiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

@Service
@Slf4j
public class FileSystemUtilService {
    @Autowired
    private AppPropertiesConfig appPropertiesConfig;


    public String getSourceFilePath(String fileName) {
        return Paths.get(appPropertiesConfig.getSourceFolder(), fileName).toString();
    }

    public String getDestinationFilePath(String fileName) {
        return Paths.get(appPropertiesConfig.getDestinationFolder(), fileName).toString();
    }

    public String getArchiveFilePath(String fileName) {
        return Paths.get(appPropertiesConfig.getArchiveFolder(), fileName).toString();
    }

    public boolean copyPasswordProtectedXlsFileToDest(String srcPath, String destPath, String password) {
        try {
            //https://www.codejava.net/coding/java-example-to-read-password-protected-excel-files-using-apache-poi
            POIFSFileSystem fileSystem = new POIFSFileSystem(new File(srcPath));
            EncryptionInfo info = new EncryptionInfo(fileSystem);
            Decryptor decryptor = Decryptor.getInstance(info);
            if (!decryptor.verifyPassword(password)) {
                log.info("Incorrect password provided");
                return false;
            }
            InputStream dataStream = decryptor.getDataStream(fileSystem);
            FileUtils.copyInputStreamToFile(dataStream, new File(destPath));
            return true;
        } catch (GeneralSecurityException ex) {
            log.error("GeneralSecurityException occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
        } catch (IOException ex) {
            log.error("IOException occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
        }  catch (Exception ex) {
            log.error("Exception occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
        }
        return false;
    }

    public boolean copyPasswordProtectedXlsxFileToDest(String srcPath, String destPath, String password) {
        try {
            FileOutputStream out = new FileOutputStream(destPath);
            Workbook workbook = WorkbookFactory.create(new File(srcPath), password);
            workbook.write(out);
            return true;
        } catch (IOException ex) {
            log.error("IOException occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
        }  catch (Exception ex) {
            log.error("Exception occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
        }
        return false;
    }

    public boolean copyFileToDest(String srcPath, String destPath) {
        try {
            File file = new File(srcPath);
            InputStream dataStream = Files.newInputStream(file.toPath());
            FileUtils.copyInputStreamToFile(dataStream, new File(destPath));
            return true;
        }catch (IOException ex) {
            log.error("IOException occurred for copying srcPath {} to destPath {} - ex {}",
                    srcPath, destPath, ex.getMessage());
            return false;
        }
    }

    public static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
