package edu.msu.mi.loom

import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.blueprints.util.io.graphml.GraphMLTokens
import grails.transaction.Transactional

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

@Transactional(readOnly = true)
class GraphParserService {
    private final Graph graph = new TinkerGraph();
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

        Map<String, List<String>> nodeStoryMap = new HashMap<>()

        String edgeId = null;
        String edgeLabel = null;
        Vertex[] edgeEndVertices = null; //[0] = outVertex , [1] = inVertex
        Map<String, Object> edgeProps = null;
        boolean inEdge = false;
        String edgeDefault

        while (reader.hasNext()) {
            Integer eventType = reader.next();
            List<String> paramList = new ArrayList<>();
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

                    paramList.add("Story1")
                    nodeStoryMap.put(vertexId, paramList)
                    if (vertexIdKey != null)
                        vertexMappedIdMap.put(vertexId, vertexId);
                    inVertex = true;
                    vertexProps = new HashMap<String, Object>();
                } else if (elementName.equals(GraphMLTokens.EDGE)) {
                    String[] vertexIds = new String[3];
                    vertexIds[0] = reader.getAttributeValue(null, GraphMLTokens.SOURCE);
                    vertexIds[1] = reader.getAttributeValue(null, GraphMLTokens.TARGET);
                    vertexIds[2] = reader.getAttributeValue(null, GraphMLTokens.DIRECTED);

                    if (edgeDefault != GraphMLTokens.DIRECTED) {
                        if (vertexIds[2] == null) {
                            nodeStoryMap.get(vertexIds[0]).add(vertexIds[1])
                            nodeStoryMap.get(vertexIds[1]).add(vertexIds[0])
                        } else {
                            nodeStoryMap.get(vertexIds[0]).add(vertexIds[1])
                        }
                    } else {
                        if (vertexIds[2] == null) {
                            nodeStoryMap.get(vertexIds[0]).add(vertexIds[1])
                        } else {
                            nodeStoryMap.get(vertexIds[0]).add(vertexIds[1])
                            nodeStoryMap.get(vertexIds[1]).add(vertexIds[0])
                        }
                    }
                } else if (elementName.equals(GraphMLTokens.DATA)) {
                    String key = reader.getAttributeValue(null, GraphMLTokens.KEY);
                    String attributeName = keyIdMap.get(key);

                    String value = reader.getElementText();
                    if (inVertex) {
                        paramList.add(value)
                        nodeStoryMap.put(vertexId, paramList)
                        if ((vertexIdKey != null) && (key.equals(vertexIdKey))) {
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

                } else if (elementName.equals(GraphMLTokens.GRAPH)) {
                    edgeDefault = reader.getAttributeValue(null, GraphMLTokens.EDGEDEFAULT);
                }
            }
        }

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
