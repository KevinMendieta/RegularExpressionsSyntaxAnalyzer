package tcom.expreg;
import tcom.ui.AstI;
/**
 * @author KevinMendieta & JossieEsteban
 */
public class RegExpTree implements AstI{

    private RegExpTree left, right;
    private String name;
    private int childs;

    public RegExpTree(String name, RegExpTree left, RegExpTree right){
        this.name = name;
        this.left = left;
        this.right = right;
        this.childs = 2;        
    }

    public RegExpTree(String name, RegExpTree left){
        this.name = name;
        this.left = left;
        this.childs = 1;
    }

    public RegExpTree(String name){
        this.name = name;
        this.childs = 0;
    }

    @Override
    public int getChildCount() {
        return childs;
    }

    @Override
    public AstI getChild(int i) {
        return i == 0 ? left : right;
    }

    @Override
    public String getName() {
        return name;
    }

}
