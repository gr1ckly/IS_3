package org.example.lab1.model.interfaces;

import java.io.InputStream;

public interface FileStorage {
    String getDownloadLink(String objectName, String originName) throws Exception;
    void uploadTmp(InputStream is, String objectName, String contentType) throws Exception;
    void saveTmp(String objectName) throws Exception;
    void deleteFile(String objectName) throws Exception;
    void deleteTmp(String objectName) throws Exception;
}
