package edu.msu.mi.loom

import edu.msu.mi.loom.file.IFileService
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Paths

@Slf4j
@Transactional
class FileService  {
    def uniqueHashService


    public String uploadFile(MultipartFile file, String filename) {
        String location = buildFileLocation(filename);
        log.debug("file: " + file)
        log.info("location was built in " + location)

        def homeDir = new File(System.getProperty("user.home"))
        File fileDest = new File(homeDir, location)
        file.transferTo(fileDest)

        return location
    }

    public void deleteFile(String location) {
//        TODO: Implement me
    }


    String readFile(MultipartFile file, boolean skipNewlines = true) {
        StringBuilder text = new StringBuilder()
        file.inputStream.eachLine { line ->
            //TODO Do something more robust here - we're removing the BOM which excel seems to insert by default
            line = line.replace("\uFEFF", "");

            text.append(line)
            if (!skipNewlines) {
                text.append("\n")
            }
        }


        return text
    }


    String buildFileLocation(String filename) {
        return buildFileLocation(uniqueHashService.getUniqueHash(), filename);
    }


    String buildFileLocation(String uniqueHash, String filename) {
        String path = Paths.get(uniqueHash).toString().replaceAll("\\\\", "/")
        def homeDir = new File(System.getProperty("user.home"))
        File file = new File(homeDir, path)
        try {
            file.mkdir();
        } catch (SecurityException Se) {
            log.error("Error while creating directory in Java:" + Se);
        }

        return path
    }
}
