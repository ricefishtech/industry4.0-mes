package com.qcadoo.view.internal.components.inputWithActionButton;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.components.FieldComponentPattern;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class InputWithActionComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/inputWithAction.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.InputWithAction";

    private boolean enabled = true;

    private String alignment;

    public InputWithActionComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();
        for (ComponentOption option : getOptions()) {
            if ("enabled".equals(option.getType())) {
                enabled = Boolean.parseBoolean(option.getValue());
            } else if ("alignment".equals(option.getType())) {
                alignment = option.getValue();
            }
        }
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new InputWithActionComponentState(this);
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = super.getJsOptions(locale);
        json.put("enabled", enabled);
        json.put("alignment", alignment);
        return json;
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
