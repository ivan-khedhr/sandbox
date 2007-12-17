/* Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wicketstuff.quickmodels.commentdemo;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.wicketstuff.quickmodels.DbPanel;
import org.wicketstuff.quickmodels.PojoCollectionModel;

/**
 * Abstract base class for a Comments panel.  Subclassed in Home.java to
 * either fetch a model containing all Comments available, or a model
 * only showing a subset of comments based on the search form.
 *
 * @author Tim Boudreau
 */
abstract class CommentsPanel extends DbPanel<Comment> {
    CommentsPanel(String id) {
        super (id);
        PojoCollectionModel<Comment> model = createModel();
        setModel (model);
        IDataProvider dataProvider = model.createDataProvider();
        
        DataView view = new DataView ("repeater", dataProvider) {
            @Override
            protected void populateItem(Item item) {
                //We just use property names - the framework will
                //automagically populate them with values from each pojo
                Label nameLbl = new Label ("name");
                item.add (nameLbl);
                Label commentLbl = new Label ("comment");
                item.add (commentLbl);
                Label dateLbl = new Label ("date");
                item.add (dateLbl);
            }
        };
        add (view);
        if (model.isEmpty()) {
            setVisible (false);
        }
    }
    
    /**
     * Create a model that determines the collection of Pojo objects to show.
     * @return The model for this component
     */
    protected abstract PojoCollectionModel<Comment> createModel();
}
