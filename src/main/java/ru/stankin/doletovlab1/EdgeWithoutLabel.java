package ru.stankin.doletovlab1;

import org.jgrapht.graph.DefaultEdge;

/**
 * Класс, созданный для того, чтобы при рендеринге графа не было меток на ребрах
 * Потому что по умолчанию библиотека их зачем-то выводит
 */
public class EdgeWithoutLabel extends DefaultEdge {

    @Override
    public String toString() {
        return "";
    }
}
