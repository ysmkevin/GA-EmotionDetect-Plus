/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Carlos
 */
public class ClassStats {

    private String name;
    private int count;
    private int correct;
    private int wrong;

    public ClassStats(String n) {
        name = n;
        count = 0;
        correct = 0;
        wrong = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void incCount() {
        count++;
    }

    public int getCorrect() {
        return correct;
    }

    public void incCorrect() {
        correct++;
    }

    public int getWrong() {
        return wrong;
    }

    public void incWrong() {
        wrong++;
    }
}

