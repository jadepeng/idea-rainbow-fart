package com.github.jadepeng.rainbowfart.extensions;

import com.github.jadepeng.rainbowfart.Context;
import com.github.jadepeng.rainbowfart.bean.Contribute;
import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Typed Action Handler
 */
public class TypedHandler extends TypedActionHandlerBase {


    private List<String> inputHistory = new ArrayList<>();


    public TypedHandler(@Nullable TypedActionHandler originalHandler) {
        super(originalHandler);
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        inputHistory.add(String.valueOf(charTyped));
        String str = StringUtils.join(inputHistory, "");
        try {
            if (Context.playCandidates(str)) {
                inputHistory.clear();
            }
        } catch (Exception e) {
            inputHistory.clear();
        }
        if (this.myOriginalHandler != null) {
            this.myOriginalHandler.execute(editor, charTyped, dataContext);
        }
    }
}
