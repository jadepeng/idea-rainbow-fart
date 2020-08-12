package com.github.jadepeng.rainbowfart.extensions;

import com.github.jadepeng.rainbowfart.Context;
import com.intellij.codeInsight.editorActions.CopyPastePostProcessor;
import com.intellij.codeInsight.editorActions.TextBlockTransferableData;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;


/**
 * CopyPastePostProcessor
 */
public class CopyPasteHanler extends CopyPastePostProcessor<TextBlockTransferableData> {

    private static final Logger LOG = Logger.getInstance(CopyPasteHanler.class);


    /**
     * This method will be run in the dispatch thread with alternative resolve enabled
     *
     * @param file
     * @param editor
     * @param startOffsets
     * @param endOffsets
     */
    @NotNull
    @Override
    public List<TextBlockTransferableData> collectTransferableData(PsiFile file, Editor editor, int[] startOffsets, int[] endOffsets) {

        StringBuilder buffer = new StringBuilder();
        Document document = editor.getDocument();
        CharSequence text = document.getCharsSequence();
        for (int i = 0; i < startOffsets.length; i++) {
            int start = startOffsets[i];
            int lineStart = document.getLineStartOffset(document.getLineNumber(start));
            int end = endOffsets[i];
            int lineEnd = document.getLineEndOffset(document.getLineNumber(end));
            buffer.append(text.subSequence(lineStart, lineEnd));
        }

        Context.onEvent("onPaste", buffer.toString());

        return Collections.emptyList();
    }
}
