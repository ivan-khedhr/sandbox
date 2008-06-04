package wicket.contrib.examples.gmap.listen.overlay.advanced;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wicket.contrib.examples.GMapExampleApplication;
import wicket.contrib.examples.WicketExamplePage;
import wicket.contrib.gmap.GMap2;
import wicket.contrib.gmap.api.GControl;
import wicket.contrib.gmap.api.GEvent;
import wicket.contrib.gmap.api.GEventHandler;
import wicket.contrib.gmap.api.GLatLng;
import wicket.contrib.gmap.api.GMarker;
import wicket.contrib.gmap.api.GMarkerOptions;
import wicket.contrib.gmap.api.GOverlay;
import wicket.contrib.gmap.event.ClickListener;

/**
 * Example HomePage for the wicket-contrib-gmap2 project.
 */
public class HomePage extends WicketExamplePage<Void>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public HomePage()
	{
		final GMap2<Object> map = new GMap2<Object>("map", GMapExampleApplication.get()
				.getGoogleMapsAPIkey());
		map.addControl(GControl.GLargeMapControl);
		add(map);
		final WebMarkupContainer<Void> repeaterParent = new WebMarkupContainer<Void>(
				"repeaterParent");
		repeaterParent.setOutputMarkupId(true);
		add(repeaterParent);
		final RepeatingView<Void> rv = new RepeatingView<Void>("label");
		rv.setOutputMarkupId(true);
		repeaterParent.add(rv);
		map.add(new ClickListener()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target, GLatLng latLng, GOverlay overlay)
			{
				if (latLng != null)
				{
					if (map.getOverlays().size() >= 3)
					{
						map.removeOverlay(map.getOverlays().get(0));
					}
					final MyMarker marker = new MyMarker(latLng, new GMarkerOptions()
							.draggable(true))
					{
						private static final long serialVersionUID = 1L;

						@Override
						GEventHandler getDragendHandler()
						{
							return new GEventHandler()
							{
								private static final long serialVersionUID = 1L;

								@Override
								public void onEvent(AjaxRequestTarget target)
								{
									target.addComponent(repeaterParent);
								}
							};
						}

						@Override
						GEventHandler getDblclickHandler()
						{
							return new GEventHandler()
							{
								private static final long serialVersionUID = 1L;

								@Override
								public void onEvent(AjaxRequestTarget target)
								{
									target.addComponent(repeaterParent);
								}
							};
						}

					};
					map.addOverlay(marker);
					marker.addListener(GEvent.dragend, marker.getDragendHandler());
					rv.removeAll();
					for (GOverlay myMarker : map.getOverlays())
					{
						final GOverlayPanel<GOverlay> label = new GOverlayPanel<GOverlay>(myMarker
								.getId(), new CompoundPropertyModel<GOverlay>(myMarker));
						label.setOutputMarkupId(true);
						rv.add(label);
					}

					target.addComponent(repeaterParent);
				}
			}
		});
	}

	/**
	 * Panel for displaying and controlling the state of a GOverlay.
	 * 
	 * @param <T>
	 */
	private static class GOverlayPanel<T> extends Panel<T>
	{
		private static final long serialVersionUID = 1L;

		public GOverlayPanel(String id, IModel<T> model)
		{
			super(id, model);
			add(new Label<GLatLng>("latLng"));
			final Label<Boolean> dragendLabel = new Label<Boolean>("dragend", new Model<Boolean>()
			{
				private static final long serialVersionUID = 1L;

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.apache.wicket.model.Model#getObject()
				 */
				@Override
				public Boolean getObject()
				{

					return ((GOverlay)getModelObject()).getListeners().containsKey(GEvent.dragend);
				}
			});
			dragendLabel.add(new AjaxEventBehavior("onclick")
			{

				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target)
				{
					MyMarker overlay = ((MyMarker)GOverlayPanel.this.getModelObject());
					if (dragendLabel.getModelObject())
					{
						overlay.clearListeners(GEvent.dragend);
					}
					else
					{
						overlay.addListener(GEvent.dragend, overlay.getDragendHandler());
					}
					target.addComponent(GOverlayPanel.this);
				}

			});
			add(dragendLabel);
			final Label<Boolean> dblclickLabel = new Label<Boolean>("dblclick",
					new Model<Boolean>()
					{
						private static final long serialVersionUID = 1L;

						@Override
						public Boolean getObject()
						{

							return ((GOverlay)getModelObject()).getListeners().containsKey(
									GEvent.dblclick);
						}
					});
			dblclickLabel.add(new AjaxEventBehavior("onclick")
			{

				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target)
				{
					MyMarker overlay = ((MyMarker)GOverlayPanel.this.getModelObject());
					if (dragendLabel.getModelObject())
					{
						overlay.clearListeners(GEvent.dblclick);
					}
					else
					{
						overlay.addListener(GEvent.dblclick, overlay.getDblclickHandler());
					}
					target.addComponent(GOverlayPanel.this);
				}

			});
			add(dblclickLabel);
		}
	}

	/**
	 * Extend a GMarker with factory methods for needed handler.
	 * 
	 */
	private static abstract class MyMarker extends GMarker
	{

		public MyMarker(GLatLng latLng, GMarkerOptions options)
		{
			super(latLng, options);
		}

		abstract GEventHandler getDblclickHandler();

		abstract GEventHandler getDragendHandler();
	}

}
