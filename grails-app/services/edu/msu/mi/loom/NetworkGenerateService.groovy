package edu.msu.mi.loom

import grails.transaction.Transactional
import org.jgrapht.Graphs
import org.jgrapht.generate.WattsStrogatzGraphGenerator
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.traverse.DepthFirstIterator
import org.jgrapht.util.SupplierUtil

import java.util.function.Supplier

@Transactional
class NetworkGenerateService {

    public class RandomCollection<E> {
        private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
        private final Random random;
        private double total = 0;

        public RandomCollection() {
            this(new Random());
        }

        public RandomCollection(Random random) {
            this.random = random;
        }

        public RandomCollection<E> add(double weight, E result) {
            if (weight <= 0) return this;
            total += weight;
            map.put(total, result);
            return this;
        }

        public E next() {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }


//    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;
//    def generateGraph(Experiment.Network_type network_type, String story, def min_degree, def max_degree, def m, def prob) {
    def generateGraph(Experiment experiment, int userCount) {


        def network_type = experiment.network_type;
        def story = experiment.story.title;
        def min_degree = experiment.min_degree;
        def max_degree = experiment.max_degree;
        def m = experiment.m;
        def prob = experiment.probability

        if(prob){
            prob = prob as float
        }


        Supplier<String> vSupplier = new Supplier<String>(){
            private int id = 0;

            @Override
            public String get()
            {
                return "v" + id++;
            }
        };
//
        def completeGraph =
                new SimpleGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
//        def generator = null;
//        switch (network_type) {
//                case Experiment.Network_type.Lattice.toString():
//                    try{
//                        generator = new RingGraphGenerator<>(userCount);
//                    }catch(Throwable t){
//                        if(log.errorEnabled) log.error("Failed to create Lattice network", t);
//                    }
//
//                    break;
//                case Experiment.Network_type.Newman_Watts.toString():
//                    try {
//                        generator = new WattsStrogatzGraphGenerator<>(userCount, max_degree, prob,true, new Random(2));
//
//
//                    }catch(Throwable t){
//                        if(log.errorEnabled) log.error("Failed to create newman-watts network", t);
//                    }
//                    break
//                case Experiment.Network_type.Barabassi_Albert.toString():
//                    try{
//                        generator = new BarabasiAlbertGraphGenerator<>(userCount,m,max_degree, new Random(2));
//                    }catch(Throwable t){
//                        if(log.errorEnabled) log.error("Failed to create Barabassi_Albert network", t);
//                    }
//
//
//                    break
//
//            }
//
//        if (generator){
//            generator.generateGraph(completeGraph);
//            Iterator<String> iter = new DepthFirstIterator<>(completeGraph);
//            while (iter.hasNext()) {
//                String vertex = iter.next();
//                List<String> paramList = new ArrayList<>();
//                ArrayList<String> neighbors = Graphs.neighborListOf(completeGraph, vertex)
//                paramList.add(story)
//                for(String d : neighbors){
//                    paramList.add(d)
//                }
//
//                nodeStoryMap.put(vertex, paramList)
//                // [n0:[Story1, n2], n1:[Story1, n2], n2:[Story1, n0, n1, n3], n3:[Story1, n2, n5, n4], n4:[Story1, n3, n6], n5:[Story1, n7], n6:[Story1, n4, n5, n8], n7:[Story1, n5, n8], n8:[Story1, n6, n7, n9, n10], n9:[Story1, n8], n10:[Story1, n8]]
//
//                System.out
//                        .println(
//                                "Vertex " + vertex + " is connected to: "
//                                        + completeGraph.edgesOf(vertex).toString());
//            }
//        }
//
//        return nodeStoryMap;
        HashMap<String, List<String>> nodeStoryMap = new HashMap<>()
        switch (network_type) {
                case Experiment.Network_type.Lattice.toString():
                    try{
                        nodeStoryMap = generateRingGraph(story, userCount, max_degree)
                    }catch(Throwable t){
                        if(log.errorEnabled) log.error("Failed to create Lattice network", t);
                    }

                    break;
                case Experiment.Network_type.Newman_Watts.toString():
                    try {
                        nodeStoryMap = generateNewWattsGraph(completeGraph, story, userCount, max_degree, prob)

                    }catch(Throwable t){
                        if(log.errorEnabled) log.error("Failed to create newman-watts network", t);
                    }
                    break
                case Experiment.Network_type.Barabassi_Albert.toString():
                    try{
                        nodeStoryMap = generateBarabasiAlbertGraph(story, userCount, min_degree,max_degree)
                    }catch(Throwable t){
                        if(log.errorEnabled) log.error("Failed to create Barabassi_Albert network", t);
                    }


                    break

            }
        return nodeStoryMap;
    }

    def generateRingGraph(String story, int min_degree, int max_degree){
        HashMap<String, List<String>> nodeStoryMap = new HashMap<>()
        def nodeRange = 0..min_degree-1
        if (min_degree == 2){
            List<String> paramList = new ArrayList<>();
            paramList.add(story)
            paramList.add("n1")
            nodeStoryMap.put("n0", paramList)

            List<String> paramList2 = new ArrayList<>();
            paramList2.add(story)
            paramList2.add("n0")
            nodeStoryMap.put("n1", paramList2)
        }else{
            for (n in nodeRange) {
                List<String> paramList = new ArrayList<>();
                paramList.add(story)
                paramList.add("n"+((n+1)%min_degree).toString())
                paramList.add("n"+((n+min_degree-1)%min_degree).toString())
                if (max_degree >2 && max_degree <= min_degree-1){
                    def maxRange = 2..max_degree
                    for (m in maxRange){
                        paramList.add("n"+((n+m)%min_degree).toString())
                    }
                }
                nodeStoryMap.put("n"+n.toString(), paramList)
            }
        }

        return nodeStoryMap

    }

    def generateNewWattsGraph(def completeGraph, String story, int min_degree, int max_degree, float prob){
        def generator = new WattsStrogatzGraphGenerator<>(min_degree, max_degree, prob,true, new Random(2));
        HashMap<String, List<String>> nodeStoryMap = new HashMap<>()
        if (generator){
            generator.generateGraph(completeGraph);
            Iterator<String> iter = new DepthFirstIterator<>(completeGraph);
            while (iter.hasNext()) {
                String vertex = iter.next();
                List<String> paramList = new ArrayList<>();
                ArrayList<String> neighbors = Graphs.neighborListOf(completeGraph, vertex)
                paramList.add(story)
                for(String d : neighbors){
                    paramList.add(d)
                }
                nodeStoryMap.put(vertex, paramList)

            }
        }
        return nodeStoryMap
    }

    def generateBarabasiAlbertGraph(String story, int m0, int m, int n, int max_degree){
        HashMap<String, List<String>> nodeStoryMap = new HashMap<>()
        if (m0 < 1) {
            throw new IllegalArgumentException("invalid initial nodes (" + m0 + " < 1)");
        } else {
            this.m0 = m0;
            if (m <= 0) {
                throw new IllegalArgumentException("invalid edges per node (" + m + " <= 0");
            } else if (m > m0) {
                throw new IllegalArgumentException("invalid edges per node (" + m + " > " + m0 + ")");
            } else {
                this.m = m;
                if (n < m0) {
                    throw new IllegalArgumentException("total number of nodes must be at least equal to the initial set");
                } else {
                    this.n = n;
                    this.rng = (Random)Objects.requireNonNull(rng, "Random number generator cannot be null");
                }
            }
        }
        // [n0:[Story1, n2], n1:[Story1, n2], n2:[Story1, n0, n1, n3], n3:[Story1, n2, n5, n4], n4:[Story1, n3, n6], n5:[Story1, n7], n6:[Story1, n4, n5, n8], n7:[Story1, n5, n8], n8:[Story1, n6, n7, n9, n10], n9:[Story1, n8], n10:[Story1, n8]]

        RandomCollection<String> rc = new RandomCollection<>()

        int j
        for(j = 0; j < m0; ++j){
            rc.add(1/m0, "n"+j.toString())
        }

        int i
        for(i = m0; i < n; ++i) {
            List<String> paramList = new ArrayList<>();
            paramList.add(story)
            int added = 0;

            while(added < m) {
                String target = rc.next()
                if(nodeStoryMap.containsKey(target)){
                    if(nodeStoryMap.get(target).size()-1 < max_degree && !paramList.contains(target)){
                        paramList.add(target)
                    }
                }else{
                    List<String> targetparamList = new ArrayList<>();
                    targetparamList.add(story)
                    targetparamList.add("n"+i.toString())
                    nodeStoryMap.put(target, targetparamList)
                    paramList.add(target)
                }






//                String node_list = nodeStoryMap.get(rng.nextInt(nodeStoryMap.size()));
//                nodeStoryMap.eachWithIndex{key, value, t ->
//
//
//                    println "$i $key: $value"}
//                if(node_list.size()-1 < max_degree){
//                    paramList.add()
//                }
//                nodeStoryMap.put(i.toString(), paramList)
//                if (!target.containsEdge(v, u)) {
//                    target.addEdge(v, u);
//                    ++added;
//                    newEndpoints.add(v);
//                    if (i > 1) {
//                        newEndpoints.add(u);
//                    }
//                }
            }
            nodeStoryMap.put("n"+i.toString(), paramList)

//            nodes.addAll(newEndpoints);
        }


        return nodeStoryMap
    }


}
