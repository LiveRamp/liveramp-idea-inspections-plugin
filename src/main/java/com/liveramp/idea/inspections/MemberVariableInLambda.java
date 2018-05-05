package com.liveramp.idea.inspections;

import java.io.Serializable;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.java.stubs.JavaClassElementType;
import com.intellij.psi.impl.java.stubs.JavaFieldStubElementType;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;


import com.liveramp.idea.inspections.fixes.FixMemberVarInLambda;

public class MemberVariableInLambda extends AbstractBaseJavaLocalInspectionTool {

  private static final Logger LOG = Logger.getInstance(MemberVariableInLambda.class);


  @Override
  public final boolean isEnabledByDefault() {
    return true;
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Fields should not be used directly in serializable lambdas";
  }

  @Override
  @Nls
  @NotNull
  public final String getGroupDisplayName() {
    return GroupNames.SERIALIZATION_GROUP_NAME;
  }

  @NotNull
  @Override
  public final PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new FindSerializableLambdaVisitor(holder, isOnTheFly);
  }

  private class FindSerializableLambdaVisitor extends JavaElementVisitor {

    ProblemsHolder holder;
    private boolean onTheFly;

    public FindSerializableLambdaVisitor(ProblemsHolder holder, boolean onTheFly) {
      this.holder = holder;
      this.onTheFly = onTheFly;
    }

    public void visitLambdaExpression(PsiLambdaExpression expression) {
      super.visitLambdaExpression(expression);
      if (expression != null) {
        PsiClassType serializableType = PsiClassType.getTypeByName(Serializable.class.getName(), expression.getProject(), GlobalSearchScope.allScope(expression.getProject()));
        PsiType lambdaType = expression.getFunctionalInterfaceType();
        if (lambdaType != null && serializableType.isAssignableFrom(lambdaType)) {
          SmartPointerManager manager = SmartPointerManager.getInstance(expression.getProject());
          SmartPsiElementPointer<PsiLambdaExpression> lambdaExpression = manager.createSmartPsiElementPointer(expression);



          expression.acceptChildren(new FindMemberVar(holder, lambdaExpression, onTheFly));
        }
      }
    }
  }

  private class FindMemberVar extends JavaRecursiveElementVisitor {

    private ProblemsHolder holder;
    private SmartPsiElementPointer<PsiLambdaExpression> lambdaExpression;
    private boolean onTheFly;

    public FindMemberVar(ProblemsHolder holder, SmartPsiElementPointer<PsiLambdaExpression> lambdaExpression, boolean onTheFly) {
      this.holder = holder;
      this.lambdaExpression = lambdaExpression;
      this.onTheFly = onTheFly;
    }

    @Override
    public void visitReferenceExpression(PsiReferenceExpression expression) {
      super.visitReferenceExpression(expression);
      PsiElement resolved = expression.resolve();
      if (resolved instanceof PsiField && !(resolved instanceof PsiEnumConstant)) {
        SmartPointerManager manager = SmartPointerManager.getInstance(expression.getProject());

        ProblemDescriptor problemDescriptor =
            holder.getManager().createProblemDescriptor(
                expression, getDisplayName(),
                new FixMemberVarInLambda(lambdaExpression,
                    manager.createSmartPsiElementPointer((PsiField)resolved),
                    manager.createSmartPsiElementPointer(expression)), ProblemHighlightType.GENERIC_ERROR, onTheFly);

        holder.registerProblem(problemDescriptor);
      }
    }
  }
}
