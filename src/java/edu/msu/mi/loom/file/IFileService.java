package edu.msu.mi.loom.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Emil Matevosyan
 * Date: 10/7/15.
 */
public interface IFileService {
    String uploadFile(MultipartFile file, String filename);

    void deleteFile(String location);

    String readFile(MultipartFile file);

    String buildFileLocation(String filename);

    String buildFileLocation(String uniqueHash, String filename);
}
