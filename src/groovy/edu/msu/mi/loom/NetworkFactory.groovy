package edu.msu.mi.loom

class NetworkFactory {

    static NetworkTemplate createNetwork(String type, Map params) {
        NetworkTemplate result = null
        switch(type) {

            case "Lattice":
                Integer degree = params.degree as Integer
                result = LatticeNetwork.findByDegree(degree)
                return result?:new LatticeNetwork(degree: params.degree as Integer).save()
            case "Barabassi_Albert":
                Integer minDegree = params.min_degree as Integer
                Integer maxDegree = params.max_degree as Integer
                Float m = params.m as Float
                result = BarabassiAlbertNetwork.findByMinDegreeAndMaxDegreeAndM(minDegree,maxDegree,m)
                return result?:new BarabassiAlbertNetwork(minDegree:minDegree as Integer,maxDegree:maxDegree,m:m).save()
            case "Newman_Watts":
                Integer minDegree = params.min_degree as Integer
                Integer maxDegree = params.max_degree as Integer
                Float prob = params.prob as Float
                result = NewmanWattsNetwork.findByMinDegreeAndMaxDegreeAndProb(minDegree,maxDegree,prob)
                return result?:new NewmanWattsNetwork(minDegree:minDegree,maxDegree:maxDegree,prob:prob).save()
            default:
                throw new Exception("Unknown network type: ${type}")
        }

    }


}
