package ru.stankin.doletovlab1;

public class SearchResults {

    public static final SearchResults NO_WAY = new SearchResults("Нет пути", -1);
    private final String path;
    private final int steps;

    public SearchResults(String path, int steps) {
        this.path = path;
        this.steps = steps;
    }

    public String getPath() {
        return path;
    }

    public int getSteps() {
        return steps;
    }
}
