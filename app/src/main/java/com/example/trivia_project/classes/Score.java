package com.example.trivia_project.classes;


public class Score  {

    private String name;
    private int score;
    private int level;

    // C'tor
    public Score(String name, int level, int score) {
        this.name = name;
        this.score = score;
        this.level = level;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", level=" + level +
                '}';
    }
}
