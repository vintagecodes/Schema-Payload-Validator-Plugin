package org.example.demo1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchemaValidatorDialog extends DialogWrapper {

    private static final Logger logger = Logger.getLogger(SchemaValidatorDialog.class.getName());
    private final JTextArea inputArea = new JTextArea(10, 40);
    private final JTextArea outputArea = new JTextArea(10, 40);

    ArrayList<String> resultfieldNamesFromSchema = new ArrayList<>();
    ArrayList<String> resultfieldNamesFromPayload = new ArrayList<>();
    ArrayList<String> fieldNamesFromSchema = new ArrayList<>();
    ArrayList<String> responses = new ArrayList<>();
    StringBuilder err = new StringBuilder();
    String responseLabel = "";
    String validateInputField = "";
    String output = "";

    public SchemaValidatorDialog() {
        super(true); // use current window as parent
        setTitle("JSON Validator");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputArea.setLineWrap(true);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);

        JBScrollPane inputScroll = new JBScrollPane(inputArea);
        JBScrollPane outputScroll = new JBScrollPane(outputArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, outputScroll);
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    protected Action[] createActions() {
        return new Action[]{
                new AbstractAction("Validate") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = inputArea.getText();
                        if (validateInputField.isEmpty() || !input.equals(validateInputField))
                        {
                             validateInputField = input;
                        }
                        output = processJsonInput(validateInputField);
                        outputArea.setText(output);
                    }
                },
                getCancelAction()
        };
    }

    private String processJsonInput(String json)
    {
        logger.log(Level.INFO,"JSON INPUT ::::: {} "+json);
        logger.log(Level.INFO, "Response LABEL :::::: {} "+responseLabel);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode schemaFields = root.get("schema").get("fields");
            JsonNode payload = root.get("payload");
            logger.log(Level.INFO, "Payload DATA ::: {}"+payload);

            StringBuilder result = new StringBuilder();

            for (JsonNode field : schemaFields) {
                String fieldName = field.get("field").asText();
                String expectedType = field.get("type").asText();
                boolean optional = field.get("optional").asBoolean();
                fieldNamesFromSchema.add(fieldName);
                JsonNode value = payload.get(fieldName);
                logger.log(Level.INFO, "Value of the Field Name from Payload :::: {}"+fieldName+"VALUE :: {}"+value);
                if (value == null)
                {
                    logger.log(Level.INFO, "Entered When fieldName contains some data :::: {} "+value);
                    if (!optional)
                    {
                        logger.log(Level.INFO, "Entered When the field is not Optional :::: ");
                        result.append("Missing required field: ").append(fieldName).append("\n");
                    }
//                    else
//                    {
//                        Iterator<Map.Entry<String, JsonNode>> payloadFields = payload.fields();
//                        while (payloadFields.hasNext())
//                        {
//                            Map.Entry<String, JsonNode> entry = payloadFields.next();
//                            String payloadFieldName = entry.getKey();
//
//                            resultfieldNamesFromPayload.add(fieldName);
//                        }
//                        logger.log(Level.INFO, "Entered When the field is Optional but not present in Schema :::: ");
//                        result.append("Unexpected field in payload : ").append(fieldName).append("\n");
//                    }
                }
                else if (!isTypeMatching(expectedType, value))
                {
                    logger.log(Level.INFO, "Entered Type Mis Match Error :::: ");
                    result.append("Type mismatch for field: ").append(fieldName)
                            .append(". Expected: ").append(expectedType)
                            .append(", Found: ").append(value.getNodeType()).append("\n");
                    logger.log(Level.INFO, "Exit Type Mis Match Error :::: ");
                }
                logger.log(Level.INFO, "Skipped from Type Mis Match Error :::: ");

                if (!result.isEmpty())
                {
                    logger.log(Level.INFO, "Entered When Result is Not Empty :::: ");
                    resultfieldNamesFromSchema.add(result.toString());
                    result.setLength(0);
                    logger.log(Level.INFO, "Existed When Result is Not Empty :::: ");
                }
            }
            logger.log(Level.INFO, "Came out from the first Loop :::: ");


            if (!resultfieldNamesFromSchema.isEmpty())
            {
                logger.log(Level.INFO, "Entered When ResultFieldNamesFromSchema is Not Empty :::: ");
                err.append("Validation Unsuccessful:\n");
                for (String errorMessage : resultfieldNamesFromSchema)
                {
                    err.append(errorMessage);
                }
                responses.add(err.toString());
                err.setLength(0);
                logger.log(Level.INFO, "Exist When ResultFieldNamesFromSchema is Not Empty :::: ");
                logger.log(Level.INFO, "Response Label :::: {}"+responseLabel);
            }
                logger.log(Level.INFO, "Entered the Iterator Loop When ResultFieldNamesFromSchema Empty :::: ");
                Iterator<Map.Entry<String, JsonNode>> payloadFields = payload.fields();
                while (payloadFields.hasNext())
                {
                    Map.Entry<String, JsonNode> entry = payloadFields.next();
                    String fieldName = entry.getKey();

                    resultfieldNamesFromPayload.add(fieldName);
                }
                logger.info("Get the field names from schema: " + fieldNamesFromSchema);
                logger.info("Get the field names from payload: " + resultfieldNamesFromPayload);
                for (String fieldName : resultfieldNamesFromPayload)
                {
                    logger.log(Level.INFO, "Entered the Second For Loop  :::: ");
                    if (!fieldNamesFromSchema.contains(fieldName))
                    {
                        logger.info("InValid FieldName from Payload: " + fieldName);
                        err.append("Unexpected field in payload : ").append(fieldName);
                        responses.add(err.toString());
                        err.setLength(0);
                    }
                }
                logger.log(Level.INFO, "Exist from the Second for Loop :::: ");
                if (!responses.isEmpty())
                {
                    logger.log(Level.INFO, "Entered When Responses are not Empty :::: ");
                    for (String errorResponse : responses)
                    {
                        err.append(errorResponse).append("\n");
                    }
                    responseLabel = "Validation Unsuccessful:\n" + err.toString();
                    err.setLength(0);
                    logger.log(Level.INFO, "Existed When Responses are not Empty :::: ");
                    logger.log(Level.INFO, "Response Label :::: {}"+responseLabel);
                }
                else
                {
                    logger.log(Level.INFO, "Entered When Responses is Empty :::: ");
                    responseLabel = "Validation Successful: Payload matches the schema.";
                    logger.log(Level.INFO, "Response Label when passes ::::: {}"+responseLabel);
                    err.setLength(0);
                    logger.log(Level.INFO, "Existed When Responses is Empty :::: ");
                    logger.log(Level.INFO, "Response Label :::: {}"+responseLabel);
                }
                logger.log(Level.INFO, "Existed from the Iterator Loop When ResultFieldNamesFromSchema Empty :::: ");

            resultfieldNamesFromPayload = new ArrayList<>();
            fieldNamesFromSchema = new ArrayList<>();
            responses = new ArrayList<>();
            resultfieldNamesFromSchema = new ArrayList<>();
        }
        catch (Exception ex)
        {
            responseLabel = "Error: " + ex.getMessage();
        }
        logger.log(Level.INFO, "Final Response Label :::: {}"+responseLabel);
        return responseLabel;
    }

    private boolean isTypeMatching(String expectedType, JsonNode value)
    {
        logger.info("Checking for type mis check: " +value + " Expected Type: " + expectedType);
        return switch (expectedType) {
            case "string" -> value.isTextual();
            case "int64", "int32" -> value.isInt() || value.isLong();
            case "boolean" -> value.isBoolean();
            case "float", "double" -> value.isFloat() || value.isDouble();
            default -> false;
        };
    }


//    public String getMessage() {
//        return "Hello from SchemaValidatorDialog!";
//    }
}
