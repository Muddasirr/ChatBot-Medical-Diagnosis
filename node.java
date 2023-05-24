import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class node<T extends Comparable<T>> {

    String data;
    node<T> left;
    node<T> right;
    ArrayList<String> Symptoms;
    ArrayList<String> causes;

    node(String d) {
        data = d;
    }

    public node<T> getLeft() {
        return left;
    }

    public node<T> getRight() {
        return right;
    }

    public String getData() {
        return data;
    }

    public ArrayList<String> getSymptoms() {
        return Symptoms;
    }

    public ArrayList<String> getCauses() {
        return causes;
    }

    public boolean hasNoChild() {
        return this.left == null && this.right == null;
    }

    public boolean hasOneChild() {
        return this.left == null || this.right == null;
    }

    public String toString() {
        return this.data + " ";
    }
}
