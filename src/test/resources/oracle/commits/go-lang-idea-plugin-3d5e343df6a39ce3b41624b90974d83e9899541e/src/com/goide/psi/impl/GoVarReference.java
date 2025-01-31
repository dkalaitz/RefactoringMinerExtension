/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.psi.impl;

import com.goide.psi.GoBlock;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoStatement;
import com.goide.psi.GoVarDefinition;
import com.goide.util.GoUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoVarReference extends GoCachedReference<GoVarDefinition> {
  private final GoBlock myPotentialStopBlock;

  public GoVarReference(@NotNull GoVarDefinition element) {
    super(element);
    myPotentialStopBlock = PsiTreeUtil.getParentOfType(element, GoBlock.class);
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {
    GoVarProcessor p = new GoVarProcessor(myElement.getText(), myElement, false);
    processResolveVariants(p);
    return p.getResult(); 
  }

  @Override
  public void processResolveVariants(@NotNull final GoScopeProcessor processor) {
    GoVarProcessor p = processor instanceof GoVarProcessor
                       ? ((GoVarProcessor)processor)
                       : new GoVarProcessor(myElement.getText(), myElement, processor.isCompletion()) {
                         @Override
                         public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
                           return super.execute(psiElement, resolveState) && processor.execute(psiElement, resolveState);
                         }
                       };

    if (myPotentialStopBlock != null) {
      if (myPotentialStopBlock.getParent() instanceof GoFunctionOrMethodDeclaration) {
        GoReference.processFunctionParameters(myElement, p);
      }
      myPotentialStopBlock.processDeclarations(p, ResolveState.initial(), PsiTreeUtil.getParentOfType(myElement, GoStatement.class),
                                               myElement);
    }
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.replace(GoElementFactory.createVarDefinitionFromText(myElement.getProject(), newElementName));
    return myElement;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return GoUtil.couldBeReferenceTo(element, myElement) && super.isReferenceTo(element);
  }
}
