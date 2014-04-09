/**
 * Created by saibi on 3/25/14.
 */

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by saibi on 3/25/14.
 */
public class Message implements Serializable {
    private int operation;
    private ArrayList<Integer> line_x;
    private ArrayList<Integer> line_y;
    public Message() {
        line_x = new ArrayList<Integer>();
        line_y = new ArrayList<Integer>();
        operation = -1;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void addPoint(int x, int y) {
        line_x.add(x);
        line_y.add(y);
    }

    public int getOperation() {
        return operation;
    }

    public ArrayList<Integer> getLine_x(){
        return line_x;
    }

    public ArrayList<Integer> getLine_y(){
        return line_y;
    }

}
