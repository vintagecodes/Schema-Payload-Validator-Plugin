package org.example.demo1;

import com.intellij.openapi.actionSystem.AnAction;

public class SchemaPayloadAction extends AnAction
{
    @Override
    public void actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent e)
    {
        SchemaValidatorDialog dialog = new SchemaValidatorDialog();
        dialog.show();
//        SchemaValidatorDialog demoPart = new SchemaValidatorDialog();
//        String message = demoPart.getMessage();
//        com.intellij.openapi.ui.Messages.showMessageDialog(e.getProject(), message, "Greeting", com.intellij.openapi.ui.Messages.getInformationIcon());
    }
}
