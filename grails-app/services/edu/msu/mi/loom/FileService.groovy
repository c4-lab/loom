package edu.msu.mi.loom

import edu.msu.mi.loom.file.IFileService
import grails.transaction.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.nio.file.Paths

@Transactional
class FileService implements IFileService {
    def uniqueHashService

    @Override
    public void uploadFile(CommonsMultipartFile file, String filename) {
        String location = buildFileLocation(filename);
        def homeDir = new File(System.getProperty("user.home"))
        File fileDest = new File(homeDir, location)
        file.transferTo(fileDest)
    }

    @Override
    public void deleteFile(String location) {
//        TODO: Implement me
    }

    @Override
    String buildFileLocation(String filename) {
        return buildFileLocation(uniqueHashService.getUniqueHash(), filename);
    }

    @Override
    String buildFileLocation(String uniqueHash, String filename) {
        return Paths.get("documents", uniqueHash, filename).toString().replaceAll("\\\\", "/");
    }
}
