package de.vogella.plugin.htmlconverter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.inject.Named;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class ConvertHandler {
    private QualifiedName path = new QualifiedName("html", "path");

    @Execute
    public void execute(Shell shell, @Optional @Named(IServiceConstants.ACTIVE_SELECTION) IStructuredSelection selection) {

            Object firstElement = selection.getFirstElement();
            if (firstElement instanceof ICompilationUnit) {
                    createOutput(shell, firstElement);

            } else {
                    MessageDialog.openInformation(shell, "Info",
                                    "Please select a Java source file");
            }
    }

    private void createOutput(Shell shell, Object firstElement) {
            String directory;
            ICompilationUnit cu = (ICompilationUnit) firstElement;
            IResource res = cu.getResource();
            boolean newDirectory = true;
            directory = getPersistentProperty(res, path);

            if (directory != null && directory.length() > 0) {
                    newDirectory = !(MessageDialog.openQuestion(shell, "Question",
                                    "Use the previous output directory?"));
            }
            if (newDirectory) {
                    DirectoryDialog fileDialog = new DirectoryDialog(shell);
                    directory = fileDialog.open();

            }
            if (directory != null && directory.length() > 0) {
                    setPersistentProperty(res, path, directory);
                    write(directory, cu);
            }
    }

    protected String getPersistentProperty(IResource res, QualifiedName qn) {
            try {
                    return res.getPersistentProperty(qn);
            } catch (CoreException e) {
                    return "";
            }
    }

    protected void setPersistentProperty(IResource res, QualifiedName qn,
                    String value) {
            try {
                    res.setPersistentProperty(qn, value);
            } catch (CoreException e) {
                    e.printStackTrace();
            }
    }

    private void write(String dir, ICompilationUnit cu) {
            try {
                    cu.getCorrespondingResource().getName();
                    String test = cu.getCorrespondingResource().getName();
                    // Need
                    String[] name = test.split("\\.");
                    String htmlFile = dir + "\\" + name[0] + ".html";
                    FileWriter output = new FileWriter(htmlFile);
                    BufferedWriter writer = new BufferedWriter(output);
                    writer.write("<html>");
                    writer.write("<head>");
                    writer.write("</head>");
                    writer.write("<body>");
                    writer.write("<pre>");
                    writer.write(cu.getSource());
                    writer.write("</pre>");
                    writer.write("</body>");
                    writer.write("</html>");
                    writer.flush();
            } catch (JavaModelException e) {
            } catch (IOException e) {
                    e.printStackTrace();
            }

    }
}
