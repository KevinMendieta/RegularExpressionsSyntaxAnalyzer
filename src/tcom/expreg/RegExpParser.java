package tcom.expreg;

import tcom.ui.*;
/**
 *
 * @author KevinMendieta & JossieEsteban
 */
public class RegExpParser {
    
    public static void main(String[] args){
        VisorExpr gui = new VisorExpr();
        gui.setParserTree(new RegExpA());
        
    }
}
