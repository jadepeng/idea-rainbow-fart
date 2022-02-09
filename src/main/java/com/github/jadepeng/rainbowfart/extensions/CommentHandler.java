package com.github.jadepeng.rainbowfart.extensions;

import com.github.jadepeng.rainbowfart.Constants;
import com.github.jadepeng.rainbowfart.Context;
import com.intellij.codeInsight.editorActions.CommentCompleteHandler;
import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;

public class CommentHandler implements CommentCompleteHandler {

    @Override
    public boolean isCommentComplete(PsiComment comment, CodeDocumentationAwareCommenter commenter, Editor editor) {
        return false;
    }

    @Override
    public boolean isApplicable(PsiComment comment, CodeDocumentationAwareCommenter commenter) {
        Context.onEvent(Constants.EVENT_COMMENTS);
        return false;
    }
}
