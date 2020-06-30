package com.github.jadepeng.rainbowfart;

import com.intellij.codeInsight.editorActions.CopyPastePostProcessor;
import com.intellij.codeInsight.editorActions.TextBlockTransferableData;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

    public class CopyPasteHanler extends CopyPastePostProcessor<TextBlockTransferableData> {
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
        return null;
    }
}
