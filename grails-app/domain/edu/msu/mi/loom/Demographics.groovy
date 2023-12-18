package edu.msu.mi.loom

class Demographics {

    String gender
    String age
    String race
    String language
    String education
    String income
    String political
    static belongsTo = [user: User]
}
