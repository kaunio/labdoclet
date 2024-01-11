package com.learnifier.labdoclet;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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

        private boolean scanMethod = false;

        public MyScanner(DocTrees docTrees) {
            treeUtils = docTrees;
        }

        @Override
        public Void visitType(TypeElement e, Integer integer) {
            scanMethod = e.getQualifiedName().toString().equals("se.dabox.cocobox.apiweb.cache.NeverCache");

            if (scanMethod) {
                System.out.println("Found it!");
            }

            //System.out.println("TYPE: "+e.getQualifiedName().toString());

            return super.visitType(e, integer);
        }

        @Override
        public Void visitExecutable(ExecutableElement e, Integer integer) {
            if (e.getSimpleName().toString().equals("CATastrohpe")) {

                System.out.println("EXEC " + e);

                String annotations = e.getReturnType().getAnnotationMirrors().stream().map(am -> am.getAnnotationType().toString()).collect(
                    Collectors.joining(","));

                System.out.println("%s: %s".formatted(e.getSimpleName(), annotations));
            }

            return super.visitExecutable(e, integer);
        }

        @Override
        public Void visitTypeParameter(TypeParameterElement e, Integer integer) {
            //System.out.println("TP");

            return super.visitTypeParameter(e, integer);
        }

        private void xscanMethod(ExecutableElement e) {
            if (!scanMethod) {
                 System.out.println("SKipping method " +e);
                return;
            }

            String annotations = e.getReturnType().getAnnotationMirrors().stream().map(am -> am.getAnnotationType().getKind().name()).collect(
                Collectors.joining(","));

            System.out.println("%s: %s".formatted(e.getSimpleName(), annotations));

        }

        private void xscanClass(Element e, Integer depth) {
            if (!(e.toString().equals("se.dabox.cocobox.apiweb.cache.NeverCache"))) {
                scanMethod = false;
                //System.out.println("Skipping "+e);
                return;
            }

            scanMethod = true;

            DocCommentTree dcTree = treeUtils.getDocCommentTree(e);
            if (dcTree != null) {
                String indent = "  ".repeat(depth);
                System.out.println(indent + "| " + e.getKind() + " " + e);
                Map<String, List<String>> tags = new TreeMap<>();
                new ShowDocTrees().scan(dcTree, 0);
            }

            scan(e.getEnclosedElements(), depth + 1);
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
