package com.example.trivia_project.classes;

import java.io.Serializable;

public class QuestionsDB implements Serializable {

    private int questionId;
    private int questionLevel;
    private String question;
    private String ans1;
    private String ans2;
    private String ans3;
    private String ans4;
    private String category;
    private int correct_ans;

    //C'tor
    public QuestionsDB(int questionId, int questionLevel, String question, String ans1, String ans2, String ans3, String ans4, int correct_ans, String category) {
        this.questionId = questionId;
        this.questionLevel = questionLevel;
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.correct_ans = correct_ans;
        this.category = category;
    }


    //Getter and Setter

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionLevel() {
        return questionLevel;
    }

    public void setQuestionLevel(int questionLevel) {
        this.questionLevel = questionLevel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public void setAns4(String ans4) {
        this.ans4 = ans4;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCorrect_ans() {
        return correct_ans;
    }

    public void setCorrect_ans(int correct_ans) {
        this.correct_ans = correct_ans;
    }

}
