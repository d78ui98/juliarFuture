package com.juliar;

import com.juliar.errors.ErrorListener;
import com.juliar.nodes.*;
import com.juliar.vistor.JuliarVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.FileInputStream;
import java.util.List;
import com.juliar.parser.*;
import static java.lang.System.out;


public class JuliarCompiler {

    public static void main(String[] args) {
        try {

            JuliarCompiler compiler = new JuliarCompiler();
            compiler.compile(args[0], args[1]);
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    public static void foo()
    {
        int x = 3;
        if (x == 6) {
            out.print(9);
        }
        int y = 0;
    }


    public void compile(String source, String ouputPath){
        try {
            FileInputStream fileInputStream = new FileInputStream(source);
            ANTLRInputStream s = new ANTLRInputStream(fileInputStream);

            juliarLexer lexer = new juliarLexer(s);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            juliarParser parser = new juliarParser(tokenStream);

            parser.removeErrorListeners();
            parser.addErrorListener(new ErrorListener());

            juliarParser.CompileUnitContext context = parser.compileUnit();
            out.println(context.toStringTree(parser));

            JuliarVisitor visitor = new JuliarVisitor();
            visitor.visit(context);

            com.juliar.codegenerator.CodeGenerator generator = new com.juliar.codegenerator.CodeGenerator();
            generator.Generate(visitor.instructions());


        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

}
