package org.wicketstuff.pickwick;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;

public class ImagePage extends WebPage {
	public ImagePage(PageParameters params) {
		String uri = params.getString("uri");
		WebComponent image = new WebComponent("scaled");
		// FIXME compute relative path instead of using absolute path
		String contextPath = getApplication().getApplicationSettings().getContextPath();
		image.add(new AttributeModifier("src", true, new Model(contextPath + "/" + TestApplication.SCALED_IMAGE_PATH + "/" + uri)));
		add(image);
	}
}
