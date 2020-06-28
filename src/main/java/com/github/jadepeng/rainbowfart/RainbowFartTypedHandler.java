package com.github.jadepeng.rainbowfart;

import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RainbowFartTypedHandler extends TypedActionHandlerBase {


    private List<String> candidates = new ArrayList<>();


    public RainbowFartTypedHandler(@Nullable TypedActionHandler originalHandler) {
        super(originalHandler);
    }

    /**
     * Processes a key typed in the editor. The handler is responsible for delegating to
     * the previously registered handler if it did not handle the typed key.
     *
     * @param editor      the editor in which the key was typed.
     * @param charTyped   the typed character.
     * @param dataContext the current data context.
     */
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        candidates.add(String.valueOf(charTyped));
        String str = StringUtils.join(candidates, "");
        try {
            List<String> voices = Context.getCandidate(str);
            if (!voices.isEmpty()) {
                Context.play(voices);
                candidates.clear();
            }
        }catch (Exception e){
            // TODO
            candidates.clear();
        }

        if (this.myOriginalHandler != null) {
            this.myOriginalHandler.execute(editor, charTyped, dataContext);
        }
    }
}
