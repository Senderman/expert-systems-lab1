package ru.stankin.doletovlab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Вершина графа
 */
public class Vertex {

    private final int value; // Значение вершины
    private final List<Vertex> children = new ArrayList<>(); //Список дочерних элементов вершины

    public Vertex(int value) {
        this.value = value;
    }

    public void addChild(Vertex vertex) {
        children.add(vertex);
    }

    public int getValue() {
        return value;
    }

    public List<Vertex> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return value == vertex.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
