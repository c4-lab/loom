package edu.msu.mi.loom.file;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by Emil Matevosyan
 * Date: 10/7/15.
 */
public interface IFileService {
    void uploadFile(CommonsMultipartFile file, String filename);

    void deleteFile(String location);

    String buildFileLocation(String filename);

    String buildFileLocation(String uniqueHash, String filename);
}
