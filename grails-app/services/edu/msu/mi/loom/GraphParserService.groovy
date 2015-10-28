package edu.msu.mi.loom

import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.util.io.graphml.GraphMLTokens
import grails.transaction.Transactional

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

@Transactional(readOnly = true)
class GraphParserService {
    private final Graph graph;
    private String vertexIdKey = null;
    private String edgeIdKey = null;
    private String edgeLabelKey = null;

    def parseGraph(def file) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        XMLStreamReader reader = inputFactory.createXMLStreamReader(file);

        Map<String, String> keyIdMap = new HashMap<String, String>();
        Map<String, String> keyTypesMaps = new HashMap<String, String>();

        Map<String, String> vertexMappedIdMap = new HashMap<String, String>();
        String vertexId = null;
        Map<String, Object> vertexProps = null;
        boolean inVertex = false;

        Map<String, String> nodeStoryMap = new HashMap<>()

        String edgeId = null;
        String edgeLabel = null;
        Vertex[] edgeEndVertices = null; //[0] = outVertex , [1] = inVertex
        Map<String, Object> edgeProps = null;
        boolean inEdge = false;

        while (reader.hasNext()) {
            Integer eventType = reader.next();
            if (eventType.equals(XMLEvent.START_ELEMENT)) {
                String elementName = reader.getName().getLocalPart();

                if (elementName.equals(GraphMLTokens.KEY)) {
                    String id = reader.getAttributeValue(null, GraphMLTokens.ID);
                    String attributeName = reader.getAttributeValue(null, GraphMLTokens.ATTR_NAME);
                    String attributeType = reader.getAttributeValue(null, GraphMLTokens.ATTR_TYPE);
                    keyIdMap.put(id, attributeName);
                    keyTypesMaps.put(attributeName, attributeType);
                } else if (elementName.equals(GraphMLTokens.NODE)) {
                    vertexId = reader.getAttributeValue(null, GraphMLTokens.ID);

                    nodeStoryMap.put(vertexId, "Story1")
                    if (vertexIdKey != null)
                        vertexMappedIdMap.put(vertexId, vertexId);
                    inVertex = true;
                    vertexProps = new HashMap<String, Object>();

//                    println "++++++++++++++++++++++++++++++++++++"
//                    println vertexId
//                    println vertexMappedIdMap
//                    println nodeStoryMap
//                    println "++++++++++++++++++++++++++++++++++++"
//                } else if (elementName.equals(GraphMLTokens.EDGE)) {
//                    edgeId = reader.getAttributeValue(null, GraphMLTokens.ID);
//                    edgeLabel = reader.getAttributeValue(null, GraphMLTokens.LABEL);
//                    edgeLabel = edgeLabel == null ? GraphMLTokens._DEFAULT : edgeLabel;
//
//                    String[] vertexIds = new String[2];
//                    vertexIds[0] = reader.getAttributeValue(null, GraphMLTokens.SOURCE);
//                    vertexIds[1] = reader.getAttributeValue(null, GraphMLTokens.TARGET);
//                    edgeEndVertices = new Vertex[2];
//
//                    for (int i = 0; i < 2; i++) { //i=0 => outVertex, i=1 => inVertex
//
//                        if (vertexIdKey == null) {
//                            edgeEndVertices[i] = graph.getVertex(vertexIds[i]);
//
//                        } else {
//                            edgeEndVertices[i] = graph.getVertex(vertexMappedIdMap.get(vertexIds[i]));
//                        }
//
//                        if (null == edgeEndVertices[i]) {
//                            edgeEndVertices[i] = graph.addVertex(vertexIds[i]);
//
//                            if (vertexIdKey != null)
//
//                            // Default to standard ID system (in case no mapped
//                            // ID is found later)
//                                vertexMappedIdMap.put(vertexIds[i], vertexIds[i]);
//                        }
//                    }
//                    inEdge = true;
//                    edgeProps = new HashMap<String, Object>();
                } else if (elementName.equals(GraphMLTokens.DATA)) {
                    String key = reader.getAttributeValue(null, GraphMLTokens.KEY);
                    String attributeName = keyIdMap.get(key);

                    if (attributeName != null) {
                        String value = reader.getElementText();
                        if (inVertex) {
                            nodeStoryMap.put(vertexId, value);
                            if ((vertexIdKey != null) && (key.equals(vertexIdKey))) {
                                // Should occur at most once per Vertex
                                // Assumes single ID prop per Vertex
//                                nodeStoryMap.put(vertexId, value);
//                                vertexId = value;
                            } else
                                vertexProps.put(attributeName, typeCastValue(key, value, keyTypesMaps));
                        } else if (inEdge) {
                            if ((edgeLabelKey != null) && (key.equals(edgeLabelKey)))
                                edgeLabel = value;
                            else if ((edgeIdKey != null) && (key.equals(edgeIdKey)))
                                edgeId = value;
                            else
                                edgeProps.put(attributeName, typeCastValue(key, value, keyTypesMaps));

                        }

                        println "================================"
                        println "nodeStoryMap" + nodeStoryMap
                        println "================================"
                    }
                }
            }
        }

        println "-----------------------------------"
        println vertexMappedIdMap.size()
        println keyIdMap
        println keyTypesMaps
        println "-----------------------------------"

        return nodeStoryMap
    }

    private static Object typeCastValue(String key, String value, Map<String, String> keyTypes) {
        String type = keyTypes.get(key);
        if (null == type || type.equals(GraphMLTokens.STRING))
            return value;
        else if (type.equals(GraphMLTokens.FLOAT))
            return Float.valueOf(value);
        else if (type.equals(GraphMLTokens.INT))
            return Integer.valueOf(value);
        else if (type.equals(GraphMLTokens.DOUBLE))
            return Double.valueOf(value);
        else if (type.equals(GraphMLTokens.BOOLEAN))
            return Boolean.valueOf(value);
        else if (type.equals(GraphMLTokens.LONG))
            return Long.valueOf(value);
        else
            return value;
    }

//    def parseGraph(def file) {
//        Graph graph = new TinkerGraph()
//        GraphMLReader reader = new GraphMLReader(graph)
//
//        reader.inputGraph(file)
//
//        Iterable<Vertex> vertices = graph.getVertices()
//        Iterator<Vertex> vertexIterator = vertices.iterator()
//        String story = graph.getAt("story")
//
//        def userCount = vertices.size()
//
//        while (vertexIterator.hasNext()) {
//            Vertex vertex = vertexIterator.next()
//            story = (String) vertex.getProperty("story") != null ?: vertex.get("story")
//            if (story == null) {
//                story = "Story1"
//            }
//            println "===================="
//            println story
//            println "===================="
//            Iterable<Edge> edges = vertex.getOutEdges()
//            Iterator<Edge> edgesIterator = edges.iterator()
//
////            while (edgesIterator.hasNext()) {
////
////                Edge edge = edgesIterator.next();
////                Vertex outVertex = edge.getOutVertex()
////                Vertex inVertex = edge.getInVertex()
////
////                String source = (String) outVertex.getProperty("id")
////                String target = (String) inVertex.getProperty("id")
//////                int since = (Integer) edge.getProperty("source")
////                String sentence = ""
////                if (edge.getDirected() == null) {
////                    sentence = source + " undirected " + target + "."
////                } else {
////                    sentence = source + " " + edge.getDirected() + " " + target + "."
////                }
////
////
////                println "-----------------------"
////                println sentence
////                println "-----------------------"
////            }
//        }
//    }
}
