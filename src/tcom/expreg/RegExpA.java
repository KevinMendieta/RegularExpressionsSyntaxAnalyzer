package tcom.expreg;

import java.io.*;
import java.util.*;
import tcom.ui.*;
import org.antlr.runtime.*;

/**
 * @author KevinMendieta & JossieEsteban
 */
public class RegExpA implements ParserTreeI{

    /* BNF usada:
     * S:: = K{+K}
     * K:: = {A[*]}
     * A:: = a | # | \ | (S)
     */

    private RegLexer lexer;
    private Token token;
    private Stack<String> stack;
    private ArrayList<String> list;
    private HashMap<String, Integer> order;
    private String string;
    private int lastIndex;

    @Override
    public AstI analizar(String srcexpr) throws VisorException {
        AstI tree = null;
        this.lastIndex = 0;
        this.string = srcexpr;
        this.stack = new Stack<String>();
        this.list = new ArrayList<String>();
        this.order = new HashMap<String,Integer>();
        this.order.put("(", 1);
        this.order.put("+", 2);
        this.order.put(".", 3);
        this.order.put("*", 4);
        try{
            //Instanciar el analizador l´exico y conectarlo con una cadena de caracteres.
            ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(srcexpr.getBytes()));
            lexer = new RegLexer(in);
            //Iniciar el proceso de parsing
            tree = parse();
        }catch(IllegalArgumentException  e){
            throw new VisorException("CARACTER INVALIDO " + string.charAt(lastIndex) + " EN LA COLUMNA " + lastIndex);
        }catch(UnsupportedEncodingException e){
            throw new VisorException("ERROR EN LA CODIFICACION DE LA ENTRADA");
        }catch(IOException e){
            throw new VisorException("ERROR EN EL I/O");
        }catch(RegParsingException e){
            throw new VisorException("ERROR EN LA EXPRESION " + string.charAt(lastIndex) + " EN LA COLUMNA " + lastIndex);
        }catch(Exception e){
            throw new VisorException("ERROR INESPERADO");
        }
        return tree;
    }

    public AstI parse() throws RegParsingException{
        token = lexer.nextToken();
        pS();
        while(!stack.isEmpty()){
            list.add(stack.pop());
        }
        if(token.getType() != lexer.EOF) throw new RegParsingException();
        return castTree();
    }

    /*
     * S:: = K{+K}
     */
    public void pS() throws RegParsingException{
        pK();
        while(token.getType() == lexer.OR){
            while(!stack.isEmpty() && order.get(stack.peek()) >= order.get(token.getText())){
                list.add(stack.pop());
            }
            stack.push(token.getText());
            lastIndex++;
            token = lexer.nextToken();
            pK();
        }
    }

    /*
     * K:: = {A[*]}
     */
    public void pK() throws RegParsingException{
        while(token.getType() == lexer.SYMBOL || token.getType() == lexer.LAMBDA || token.getType() == lexer.LPAR){
            pA();
            if(token.getType() == lexer.STAR){
                list.add("*");
                lastIndex++;
                token = lexer.nextToken();
            }
            if(token.getType() == lexer.SYMBOL || token.getType() == lexer.LAMBDA || token.getType() == lexer.LPAR){
                while(!stack.isEmpty() && order.get(stack.peek()) >= order.get(".")){
                    list.add(stack.pop());
                }
                stack.push(".");
            }
        }
    }

    /*
     * A:: = a | # | \ | (S)
     */
    public void pA() throws RegParsingException{
        if(token.getType() == RegLexer.SYMBOL || token.getType() == RegLexer.LAMBDA){
            list.add(token.getText());
            lastIndex++;
            token = lexer.nextToken();
        }else if(token.getType() == RegLexer.LPAR){
            stack.add("(");
            lastIndex++;
            token = lexer.nextToken();
            pS();
            if(token.getType() != RegLexer.RPAR)throw new RegParsingException();
            String top = stack.pop();
            while(!top.equals("(")){
                list.add(top);
                top = stack.pop();
            }
            lastIndex++;
            token = lexer.nextToken();
        }else{
            throw new RegParsingException();
        }
    }

    public AstI castTree(){
        Stack<RegExpTree> path = new Stack<RegExpTree>();
        for(int i  = 0; i < list.size(); i++){
            if(order.get(list.get(i)) == null){
                RegExpTree node = new RegExpTree(list.get(i));
                path.add(node);
            }else{
                RegExpTree node = null;
                if(list.get(i).equals("*")){
                    node = new RegExpTree(list.get(i), path.pop());
                }else{
                    RegExpTree second = path.pop();
                    RegExpTree first = path.pop();
                    node = new RegExpTree(list.get(i), first, second);
                }
                path.push(node);
            }
        }
        return path.pop();
    }

}
