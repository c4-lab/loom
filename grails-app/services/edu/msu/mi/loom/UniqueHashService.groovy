package edu.msu.mi.loom

class UniqueHashService {
    private final int LENGTH = 12
    def randomStringGenerator

    String getUniqueHash() {
        def newHash = null
        int count = 0;
        while (newHash == null || uniqueHashExists(newHash)) {
            newHash = randomStringGenerator.generateLowercase(LENGTH)
            if (++count == 10) {
                throw new IllegalStateException("Something is wrong on generating unique hash. Tried already 10 times to get an unique one, and still nothing.");
            }
        }

        def uniqueHash = new UniqueHash(hash: newHash).save(failOnError: true)

        return uniqueHash.hash
    }

    private static boolean uniqueHashExists(String hash) {
        return UniqueHash.countByHash(hash) > 0;
    }
}
