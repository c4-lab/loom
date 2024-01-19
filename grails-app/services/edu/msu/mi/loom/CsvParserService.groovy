package edu.msu.mi.loom

import au.com.bytecode.opencsv.CSVReader
import grails.transaction.Transactional

@Transactional
class CsvParserService {

    List<Map<String, String>> parseCsvFile(String csvContent) {
        CSVReader reader = new CSVReader(new StringReader(csvContent))
        List<String[]> lines = reader.readAll()
        reader.close()

        if (lines.size() < 1) {
            return []
        }

        def header = lines[0]
        def result = []
        lines[1..-1].each { parsedLine ->
            def record = [:]
            header.eachWithIndex { column, index ->
                record[column] = parsedLine[index]
            }
            result << record
        }

        return result
    }
}
