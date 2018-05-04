package com.liveramp.idea.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class FixMemberVarInLambda implements LocalQuickFix {

  private final SmartPsiElementPointer<PsiField> field;
  private final SmartPsiElementPointer<PsiReferenceExpression> fieldUseSite;
  private SmartPsiElementPointer<PsiLambdaExpression> lambdaExpression;

  public FixMemberVarInLambda(
      SmartPsiElementPointer<PsiLambdaExpression> lambdaExpression,
      SmartPsiElementPointer<PsiField> field,
      SmartPsiElementPointer<PsiReferenceExpression> fieldUseSite) {
    this.field = field;
    this.fieldUseSite = fieldUseSite;
    this.lambdaExpression = lambdaExpression;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return "Alias field as local final variable";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

    PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

    String prefix = field.getElement().getContainingFile().equals(fieldUseSite.getElement().getContainingFile()) ? "this." : "";

    String fieldName = field.getElement().getName();

    PsiDeclarationStatement newLocalFinalVariable =
        factory.createVariableDeclarationStatement(fieldName, field.getElement().getType(),
            factory.createExpressionFromText(prefix+ fieldName, null));

    final PsiLocalVariable newVariable = (PsiLocalVariable)newLocalFinalVariable.getDeclaredElements()[0];
    newVariable.getModifierList().setModifierProperty(PsiModifier.FINAL, true);

    PsiElement anchor = lambdaExpression.getElement();
    PsiElement parent = anchor.getParent();
    while (!(parent instanceof PsiCodeBlock)){
      anchor = parent;
      parent = parent.getParent();
    }
    parent.addBefore(newLocalFinalVariable, anchor);

    fieldUseSite.getElement().bindToElement(newVariable);
  }
}
