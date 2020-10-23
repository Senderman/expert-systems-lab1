package ru.stankin.doletovlab1;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    private final List<Vertex> vertices; // Список вершин графа
    private final BufferedImage graphImage; // Изображение графа

    public Graph(int[][] matrix) {
        // Конвертировать матрицу смежности в граф
        this.vertices = new ArrayList<>(matrix.length);
        DefaultDirectedGraph<Integer, EdgeWithoutLabel> jGraph = new DefaultDirectedGraph<>(EdgeWithoutLabel.class);
        for (int i = 0; i < matrix.length; i++) {
            vertices.add(new Vertex(i));
            jGraph.addVertex(i);
        }

        for (int vIndex = 0; vIndex < matrix.length; vIndex++) {
            Vertex parent = vertices.get(vIndex);
            for (int childIndex = 0; childIndex < matrix.length; childIndex++) {
                if (matrix[vIndex][childIndex] != 0) {
                    parent.addChild(vertices.get(childIndex));
                    jGraph.addEdge(parent.getValue(), vertices.get(childIndex).getValue());
                }
            }
        }

        // Рендеринг графа в картинку
        JGraphXAdapter<Integer, EdgeWithoutLabel> adapter = new JGraphXAdapter<>(jGraph);
        mxIGraphLayout layout = new mxCircleLayout(adapter);
        layout.execute(adapter.getDefaultParent());
        this.graphImage = mxCellRenderer.createBufferedImage(adapter, null, 1.5, Color.WHITE, true, null);
    }

    public BufferedImage getGraphImage() {
        return graphImage;
    }

    /**
     * Поиск в ширину
     *
     * @param start начальная вершина
     * @param goal  конечная вершина
     * @return результаты поиска
     */
    public SearchResults breadthSearch(int start, int goal) {

        List<Vertex> open = new ArrayList<>();
        Set<Vertex> closed = new HashSet<>(); // в hashset поиск за O(1), индекс нам не важен
        Map<Vertex, Vertex> pathMap = new HashMap<>(); // пары "вершина-родитель"
        int steps = 0;
        var startVertex = vertices.get(start);
        var goalVertex = vertices.get(goal);
        open.add(startVertex);

        while (!open.isEmpty()) {
            var currentVertex = open.remove(0);
            steps++;
            if (currentVertex.equals(goalVertex)) {
                return new SearchResults(findPath(pathMap, startVertex, goalVertex), steps);
            }
            closed.add(currentVertex);

            for (var child : currentVertex.getChildren()) {
                if (open.contains(child) || closed.contains(child)) continue;
                pathMap.put(child, currentVertex);
                open.add(child);
            }
        }
        return SearchResults.NO_WAY;
    }

    /**
     * Поиск в глубину на списках
     *
     * @param start начальная вершина
     * @param goal  конечная вершина
     * @return результаты поиска
     */
    public SearchResults listDepthSearch(int start, int goal) {
        List<Vertex> open = new ArrayList<>();
        Set<Vertex> closed = new HashSet<>(); // в hashset поиск за O(1), индекс нам не важен
        Map<Vertex, Vertex> pathMap = new HashMap<>(); // пары "вершина-родитель"
        int steps = 0;

        var startVertex = vertices.get(start);
        var goalVertex = vertices.get(goal);
        open.add(startVertex);

        while (!open.isEmpty()) {
            var currentVertex = open.remove(0);
            steps++;
            if (currentVertex.equals(goalVertex)) {
                return new SearchResults(findPath(pathMap, startVertex, goalVertex), steps);
            }
            closed.add(currentVertex);
            List<Vertex> childrenToAdd = new ArrayList<>();
            for (var child : currentVertex.getChildren()) {
                pathMap.put(child, currentVertex);
                if (open.contains(child) || closed.contains(child)) continue;
                childrenToAdd.add(child);
            }
            open.addAll(0, childrenToAdd);
        }
        return SearchResults.NO_WAY;
    }

    /**
     * Рекурсивный поиск в глубину (основная функция)
     *
     * @param start начальная вершина
     * @param goal  конечная вершина
     * @return результаты поиска
     */
    public SearchResults recurseDepthSearch(int start, int goal) {
        Set<Vertex> closed = new HashSet<>(); // в hashset поиск за O(1), индекс нам не важен
        Map<Vertex, Vertex> pathMap = new HashMap<>(); // пары "вершина-родитель"
        var startVertex = vertices.get(start);
        var goalVertex = vertices.get(goal);
        int steps = recurseDepthSearch(startVertex, goalVertex, closed, pathMap, 0);
        if (steps == -1) return SearchResults.NO_WAY;
        return new SearchResults(findPath(pathMap, startVertex, goalVertex), steps);
    }

    /**
     * Рекурсивный поиск в глубину (функция, вызываемая рекурсивно)
     *
     * @param start   начальная вершина
     * @param goal    конечная вершина
     * @param closed  список CLOSED
     * @param steps   кол-во шагов, изначально 0
     * @param pathMap пары "вершина-родитель"
     * @return кол-во потраченных шагов, либо -1 если путь не существует
     */
    private int recurseDepthSearch(Vertex start, Vertex goal, Set<Vertex> closed, Map<Vertex, Vertex> pathMap, int steps) {
        if (start.equals(goal)) return steps + 1;

        closed.add(start);
        for (var child : start.getChildren()) {
            pathMap.put(child, start);
            if (closed.contains(child)) continue;
            int newSteps = recurseDepthSearch(child, goal, closed, pathMap, steps + 1);
            if (newSteps != -1)
                return newSteps;
        }
        return -1;
    }

    /**
     * Модифицированный поиск в глубину с нахождением пути во время поиска
     *
     * @param start начальная вершина
     * @param goal  конечная вершина
     * @return результаты поиска
     */
    public SearchResults modRecurseDepthSearch(int start, int goal) {
        Set<Vertex> closed = new HashSet<>(); // в hashset поиск за O(1), индекс нам не важен
        List<Vertex> path = new ArrayList<>(); // путь
        var startVertex = vertices.get(start);
        var goalVertex = vertices.get(goal);

        int steps = modRecurseDepthSearch(startVertex, goalVertex, path, closed, 0);
        if (steps == -1) return SearchResults.NO_WAY;

        String pathString = path.stream().map(Vertex::toString).collect(Collectors.joining("-"));
        return new SearchResults(pathString, steps);
    }

    /**
     * Модифицированный поиск в глубину с нахождением пути во время поиска (функция, вызываемая рекурсивно)
     *
     * @param start  начальная вершина
     * @param goal   конечная вершина
     * @param path   список-путь
     * @param closed список CLOSED
     * @param steps  кол-во шагов, изначально 0
     * @return кол-во шагов, или -1 если путь не существует
     */
    private int modRecurseDepthSearch(Vertex start, Vertex goal, List<Vertex> path, Set<Vertex> closed, int steps) {
        path.add(start);
        if (start.equals(goal)) return steps + 1;
        closed.add(start);

        for (var child : start.getChildren()) {
            if (closed.contains(child)) continue;
            int newSteps = modRecurseDepthSearch(child, goal, path, closed, steps + 1);
            if (newSteps != -1)
                return newSteps;
            else
                path.remove(path.size() - 1);
        }
        return -1;
    }

    /**
     * Найти путь по заданным парам "вершина-родитель"
     *
     * @param pathMap пары "вершина-родитель"
     * @param start   начальная вершина
     * @param goal    конечная вершина
     * @return сведения о пути
     */
    private String findPath(Map<Vertex, Vertex> pathMap, Vertex start, Vertex goal) {
        List<Integer> result = new ArrayList<>();
        var child = goal;
        result.add(child.getValue());
        while (!child.equals(start)) {
            var parent = pathMap.get(child);
            result.add(0, parent.getValue());
            child = parent;
        }
        // превратить список в строку элементов разделенных через дефис
        return result.stream().map(String::valueOf).collect(Collectors.joining("-"));
    }

}
