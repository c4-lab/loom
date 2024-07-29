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
     * Generates the edges for the graph and persists them.
     * @param session
     * @param numVertices
     * @param latticeNetwork
     */
    def generateGraph(Session session, int numVertices, LatticeNetwork latticeNetwork) {

        //TODO: Fix API later
        session = Session.get(session.id)
        int degree = latticeNetwork.degree

        if (degree % 2 != 0 || degree <= 0) {
            throw new IllegalArgumentException("Degree should be a positive even number.")
        }

        if (numVertices < degree) {
            throw new IllegalArgumentException("The number of vertices should be at least equal to the specified degree.")
        }

        // Connect vertices to form a ring
        Set edges = new HashSet()
        List<String> nodes = new ArrayList<String>()

        for (int i = 0; i < numVertices; i++) {
            def nodeName = "n${i}"
            nodes.add(nodeName)

            // Generate neighbors based on specified degree
            List<String> neighbors = []
            for (int j = 1; j <= degree / 2; j++) {
                neighbors.add("n${(i + j) % numVertices}")
                neighbors.add("n${(i - j + numVertices) % numVertices}")
            }

            neighbors.each {
                edges.add([nodeName, it] as Set)
            }

        }
        // Persist edges
        edges.each { Set ends ->
            new Edge(ends: ends, session: session).save()
        }

        session.save(flush: true)

        return nodes
    }



    def generateGraph(Session session, int numVertices, BarabassiAlbertNetwork barabassiAlbertNetwork) {
        throw new UnsupportedOperationException("Barabassi-Albert networks are not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    def generateGraph( Session session, int numVertices, NewmanWattsNetwork newmanWattsNetwork) {
        throw new UnsupportedOperationException("Newman-Watts networks are not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
