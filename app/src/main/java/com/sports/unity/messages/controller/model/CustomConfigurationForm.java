package com.sports.unity.messages.controller.model; /**
 *
 * Copyright the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ChildrenAssociationPolicy;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.ConfigureNodeFields;
import org.jivesoftware.smackx.pubsub.ItemReply;
import org.jivesoftware.smackx.pubsub.NodeType;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * A decorator for a {@link Form} to easily enable reading and updating
 * of node configuration.  All operations read or update the underlying {@link DataForm}.
 *
 * <p>Unlike the {@link Form}.setAnswer(XXX)} methods, which throw an exception if the field does not
 * exist, all <b>ConfigureForm.setXXX</b> methods will create the field in the wrapped form
 * if it does not already exist.
 *
 * @author Robin Collier
 */
public class CustomConfigurationForm extends ConfigureForm {

    public CustomConfigurationForm(DataForm.Type formType) {
        super(formType);
    }

    public void addField(String fieldName, FormField.Type type) {
        if (getField(fieldName) == null) {
            FormField field = new FormField(fieldName);
            field.setType(type);
            addField(field);
        }
    }

}
