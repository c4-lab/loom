package edu.msu.mi.loom

import au.com.bytecode.opencsv.CSVWriter

class ExportService {
    static transactional = false

    private static
    final String FILE_HEADER = "user,session,training time,simulation score,experiment score by rounds,tiles order"
    private static final String COMMA_DELIMITER = ","
    private static final String NEW_LINE_SEPARATOR = "\n"

    def writeCSV(List<UserStatistic> stats) {
        Writer writer = new StringWriter()

        def w = new CSVWriter(writer)
        w.writeNext((String[]) ['user', 'session', 'training time', 'simulation score', 'experiment score by rounds', 'tiles order'])

        stats.each { stat ->
            String[] line = [String.valueOf(stat.user.id), String.valueOf(stat.session.id), String.valueOf(stat.trainingTime),
                             String.valueOf(stat.simulationScore), String.valueOf(stat.experimentRoundScore), String.valueOf(stat.textOrder)]
            w.writeNext(line)
        }

        writer.close()
        return writer.toString()

//        FileWriter fileWriter = null
//
//        try {
//            fileWriter = new FileWriter("test.csv")
//            fileWriter.append(FILE_HEADER.toString())
//            fileWriter.append(NEW_LINE_SEPARATOR)
//
//            for (UserStatistic stat : stats) {
//                fileWriter.append(String.valueOf(stat.user.id))
//                fileWriter.append(COMMA_DELIMITER)
//                fileWriter.append(String.valueOf(stat.session.id))
//                fileWriter.append(COMMA_DELIMITER)
//                fileWriter.append(String.valueOf(stat.trainingTime))
//                fileWriter.append(COMMA_DELIMITER)
//                fileWriter.append(String.valueOf(stat.simulationScore))
//                fileWriter.append(COMMA_DELIMITER)
//                fileWriter.append(String.valueOf(stat.experimentRoundScore))
//                fileWriter.append(COMMA_DELIMITER)
//                fileWriter.append(String.valueOf(stat.textOrder))
//                fileWriter.append(NEW_LINE_SEPARATOR)
//            }
//
//            return fileWriter.toString()
//        } catch (Exception e) {
//            System.out.println("Error in CsvFileWriter !!!");
//            e.printStackTrace();
//        } finally {
//
//            try {
//                fileWriter.flush();
//                fileWriter.close();
//            } catch (IOException e) {
//                System.out.println("Error while flushing/closing fileWriter !!!");
//                e.printStackTrace();
//            }
//        }
    }
}
