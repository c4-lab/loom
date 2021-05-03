package edu.msu.mi.loom

class TrainingSet {


    static hasMany = [simulations: Simulation, trainings: Training, readings:Reading, surveys:Survey, HITId: String, HITTypeId: String]

    String name
    List<Training> trainings
    String qualifier
//    int HIT_num
    float training_payment
    List<String> HITId = new ArrayList<>()
    List<String> HITTypeId = new ArrayList<>()
    int uiflag = 0
    int paid = 0
    int total = 0

    static constraints = {
        name blank: false, unique: true
        simulations nullable: true
        trainings nullable: true
        readings nullable: true
        surveys nullable: true
        qualifier nullable: true
    }


    static String constructQualificationString(TrainingSet ts) {
        "loomtrainings${ts.id}"
    }
}
