package com.learnifier.labdoclet;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.ElementScanner14;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTreeScanner;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class LabDoclet implements Doclet {
    @Override
    public void init(Locale locale, Reporter reporter) {

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        // This method is called to perform the work of the doclet.
        // In this case, it just prints out the names of the
        // elements specified on the command line.
        environment.getSpecifiedElements()
            .forEach(e -> {
                MyScanner scanner = new MyScanner(environment.getDocTrees());
                scanner.scan(e, 0);

            });



        return true;
    }

    private static class MyScanner extends ElementScanner14<Void,Integer> {
        private final DocTrees treeUtils;

        public MyScanner(DocTrees docTrees) {
            treeUtils = docTrees;
        }

        @Override
        public Void scan(Element e, Integer depth) {
            if (!(e.getKind() == ElementKind.CLASS && e.toString().equals("se.dabox.cocobox.apiweb.cache.NeverCache"))) {
                return super.scan(e, depth + 1);
            }

            DocCommentTree dcTree = treeUtils.getDocCommentTree(e);
            if (dcTree != null) {
                String indent = "  ".repeat(depth);
                System.out.println(indent + "| " + e.getKind() + " " + e);
                Map<String, List<String>> tags = new TreeMap<>();
                new ShowDocTrees().scan(dcTree, 0);
            }
            return super.scan(e, depth + 1);
        }
    }

    static class ShowDocTrees extends DocTreeScanner<Void, Integer> {

        @Override
        public Void scan(DocTree t, Integer depth) {
            String indent = "  ".repeat(depth);

            System.out.println(indent + "# "
                + t.getKind() + " "
                + t.toString().replace("\n", "\n" + indent + "#    "));
            return super.scan(t, depth + 1);
        }
    }
}
