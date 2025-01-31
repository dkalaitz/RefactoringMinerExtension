/*
 * Copyright 2003-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.ide.projectView;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import org.jetbrains.annotations.NotNull;

@State(
    name="ProjectView",
    storages= {
        @Storage(
            file = StoragePathMacros.WORKSPACE_FILE
        )}
)
public class ProjectViewImpl extends com.intellij.ide.projectView.impl.ProjectViewImpl {
  public ProjectViewImpl(@NotNull Project project,
      FileEditorManager fileEditorManager, ToolWindowManagerEx toolWindowManager) {
    super(project, fileEditorManager, toolWindowManager);
  }

  @Override
  protected boolean isShowMembersOptionSupported() {
    return false;
  }
}
