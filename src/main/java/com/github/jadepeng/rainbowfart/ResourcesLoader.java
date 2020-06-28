package com.github.jadepeng.rainbowfart;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;


/**
 *  资源加载
 * @author jqpeng
 */
public class ResourcesLoader implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        Context.loadConfig();
    }
}
