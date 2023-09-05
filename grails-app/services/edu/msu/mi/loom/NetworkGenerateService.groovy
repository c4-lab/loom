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

    /**
     * Generates the edges for the graph and persists them.  Note that degree is currently ignored
     * TODO: Make degree work
     * @param session
     * @param numVertices
     * @param degree
     */
    def generateGraph(Session session, int numVertices, LatticeNetwork latticeNetwork) {
        if (numVertices < 4) {
            throw new IllegalArgumentException("The number of vertices should be at least 4 to create a ring graph with 4 neighbors for each vertex.")
        }


        // Connect vertices to form a ring
        Set edges = new HashSet()
        List<String> nodes = new ArrayList<String>()
        for (int i = 0; i < numVertices; i++) {
            def nodeName = "n${i}"
            nodes.add(nodeName)
            def neighbors = [
                    "n${(i + 1) % numVertices}",
                    "n${(i + 2) % numVertices}",
                    "n${(i - 1 + numVertices) % numVertices}",
                    "n${(i - 2 + numVertices) % numVertices}"
            ]
            neighbors.each {
                edges.add([nodeName, it] as Set)
            }
            edges.each { Set ends ->
                Edge.create(ends: ends, session: session).save(flush: true)
            }

        }
        return nodes


    }


    def generateGraph(Session session, int numVertices, BarabassiAlbertNetwork barabassiAlbertNetwork) {
        throw new UnsupportedOperationException("Barabassi-Albert networks are not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    def generateGraph( Session session, int numVertices, NewmanWattsNetwork newmanWattsNetwork) {
        throw new UnsupportedOperationException("Newman-Watts networks are not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
