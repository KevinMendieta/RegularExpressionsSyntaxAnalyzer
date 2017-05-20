package tcom.expreg;

import java.io.ByteArrayInputStream;
import tcom.ui.*;
import org.antlr.runtime.*;

/**
 * @author KevinMendieta & JossieEsteban
 */
public class RegExpA implements ParserTreeI{
    
    RegLexer lexer;
    Token token;
    
    @Override
    public AstI analizar(String srcexpr) throws VisorException {
        AstI tree = new RegExpTree();
        try{
            //Instanciar el analizador l´exico y conectarlo con una cadena de caracteres.
            ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(srcexpr.getBytes()));
            lexer = new RegLexer(in);
            //Iniciar el proceso de parsing
            //tree = parse();
        }catch(Exception e){
            throw new VisorException(e.getMessage());
        }
        return null;
    }
    
}
