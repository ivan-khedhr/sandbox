package wicket.contrib.examples.gmap;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import wicket.contrib.examples.WicketExamplePage;
import wicket.contrib.gmap.GMap2;
import wicket.contrib.gmap.GMapHeaderContributor;
import wicket.contrib.gmap.api.GClientGeocoder;
import wicket.contrib.gmap.api.GControl;
import wicket.contrib.gmap.api.GInfoWindowTab;
import wicket.contrib.gmap.api.GLatLng;
import wicket.contrib.gmap.api.GMapType;
import wicket.contrib.gmap.api.GMarker;
import wicket.contrib.gmap.api.GMarkerOptions;
import wicket.contrib.gmap.api.GPolygon;
import wicket.contrib.gmap.api.GPolyline;
import wicket.contrib.gmap.event.ClickListener;
import wicket.contrib.gmap.event.InfoWindowCloseListener;
import wicket.contrib.gmap.event.InfoWindowOpenListener;
import wicket.contrib.gmap.event.MoveEndListener;
import wicket.contrib.gmap.util.GeocoderException;

/**
 * Example HomePage for the wicket-contrib-gmap2 project
 */
public class HomePage extends WicketExamplePage {

	private static final long serialVersionUID = 1L;

	private final FeedbackPanel feedback;

	private final Label markerLabel;

	private final Label zoomLabel;

	private final Label center;

	private MoveEndListener moveEndBehavior;

	public HomePage() {
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);

		final GMap2 topMap = new GMap2("topPanel", LOCALHOST);
		topMap.setDoubleClickZoomEnabled(true);
		topMap.add(new MoveEndListener() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onMoveEnd(AjaxRequestTarget target) {
				target.addComponent(zoomLabel);
			}
		});
		topMap.add(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target, GLatLng latLng,
					GMarker marker) {
				if (marker != null) {
					topMap.getInfoWindow().open(marker, new HelloPanel());
				} else if (latLng != null) {
					marker = new GMarker(latLng);
					topMap.addOverlay(marker);
				}
				markerSelected(target, marker);
			}
		});
		topMap.setZoom(10);
		GMarkerOptions options = new GMarkerOptions();
		options.setTitle("Home");
		options.setDraggable(true);
		options.setAutoPan(true);
		topMap.addOverlay(new GMarker(new GLatLng(37.4, -122.1), options));
		topMap.addOverlay(new GPolygon("#000000", 4, 0.7f, "#E9601A", 0.7f,
				new GLatLng(37.3, -122.4), new GLatLng(37.2, -122.2),
				new GLatLng(37.3, -122.0), new GLatLng(37.4, -122.2),
				new GLatLng(37.3, -122.4)));
		topMap.addOverlay(new GPolyline("#FFFFFF", 8, 1.0f, new GLatLng(37.35,
				-122.3), new GLatLng(37.25, -122.25),
				new GLatLng(37.3, -122.2), new GLatLng(37.25, -122.15),
				new GLatLng(37.35, -122.1)));
		topMap.addControl(GControl.GLargeMapControl);
		topMap.addControl(GControl.GMapTypeControl);
		add(topMap);

		zoomLabel = new Label("zoomLabel", new PropertyModel(topMap, "zoom"));
		zoomLabel.add(topMap.new SetZoom("onclick", 10));
		zoomLabel.setOutputMarkupId(true);
		add(zoomLabel);

		markerLabel = new Label("markerLabel", new Model(null));
		markerLabel.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.ajax.AjaxEventBehavior#onEvent(org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				GMarker marker = (GMarker) markerLabel.getModelObject();
				if (marker != null) {
					GLatLng point = marker.getLagLng();

					GMarker random = new GMarker(new GLatLng(point.getLat()
							* (0.9995 + Math.random() / 1000), point.getLng()
							* (0.9995 + Math.random() / 1000)));

					topMap.addOverlay(random);
				}
			}
		});
		add(markerLabel);

		final Label zoomIn = new Label("zoomInLabel", "ZoomIn");
		zoomIn.add(topMap.new ZoomIn("onclick"));
		add(zoomIn);

		final Label zoomOut = new Label("zoomOutLabel", "ZoomOut");
		zoomOut.add(topMap.new ZoomOut("onclick"));
		add(zoomOut);

		final GMap2 bottomMap = new GMap2("bottomPanel",
				new GMapHeaderContributor(LOCALHOST));
		bottomMap.setOutputMarkupId(true);
		bottomMap.setMapType(GMapType.G_SATELLITE_MAP);
		bottomMap.setScrollWheelZoomEnabled(true);
		moveEndBehavior = new MoveEndListener() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onMoveEnd(AjaxRequestTarget target) {
				target.addComponent(center);
			}
		};
		bottomMap.add(moveEndBehavior);
		bottomMap.add(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target, GLatLng gLatLng,
					GMarker marker) {
				if (gLatLng != null) {
					bottomMap.getInfoWindow().open(gLatLng, new HelloPanel());
				}
			}

		});
		bottomMap.add(new InfoWindowCloseListener() {
			@Override
			protected void onInfoWindowClose(AjaxRequestTarget target) {
				info("InfoWindow was closed");
				target.addComponent(feedback);
			}
		});
		bottomMap.add(new InfoWindowOpenListener() {
			@Override
			protected void onInfoWindowOpen(AjaxRequestTarget target) {
				info("InfoWindow was opened");
				target.addComponent(feedback);
			}
		});
		bottomMap.addControl(GControl.GSmallMapControl);
		bottomMap.getInfoWindow().open(new GLatLng(37.5, -122.1),
				new GInfoWindowTab("One", new HelloPanel()),
				new GInfoWindowTab("Two", new HelloPanel()));
		add(bottomMap);

		center = new Label("center", new PropertyModel(bottomMap, "center"));
		center.add(bottomMap.new SetCenter("onclick", new GLatLng(37.5, -122.1,
				false)));
		center.setOutputMarkupId(true);
		add(center);

		final Label n = new Label("n", "N");
		n.add(bottomMap.new PanDirection("onclick", 0, 1));
		add(n);

		final Label ne = new Label("ne", "NE");
		ne.add(bottomMap.new PanDirection("onclick", -1, 1));
		add(ne);

		final Label e = new Label("e", "E");
		e.add(bottomMap.new PanDirection("onclick", -1, 0));
		add(e);

		final Label se = new Label("se", "SE");
		se.add(bottomMap.new PanDirection("onclick", -1, -1));
		add(se);

		final Label s = new Label("s", "S");
		s.add(bottomMap.new PanDirection("onclick", 0, -1));
		add(s);

		final Label sw = new Label("sw", "SW");
		sw.add(bottomMap.new PanDirection("onclick", 1, -1));
		add(sw);

		final Label w = new Label("w", "W");
		w.add(bottomMap.new PanDirection("onclick", 1, 0));
		add(w);

		final Label nw = new Label("nw", "NW");
		nw.add(bottomMap.new PanDirection("onclick", 1, 1));
		add(nw);

		final Label infoWindow = new Label("infoWindow", "openInfoWindow");
		infoWindow.add(new AjaxEventBehavior("onclick") {
			/**
			 * @see org.apache.wicket.ajax.AjaxEventBehavior#onEvent(org.apache.wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				bottomMap.getInfoWindow().open(new GLatLng(37.5, -122.1),
						new HelloPanel());
			}
		});
		add(infoWindow);
		add(new Link("reload") {
			@Override
			public void onClick() {
			}
		});
		final Label enabledMoveEnd = new Label("enabledMoveEnd",
				"the move end behavior is enabled:");
		add(enabledMoveEnd);
		final Label enabledLabel = new Label("enabled", new Model() {
			@Override
			public Object getObject() {
				return bottomMap.getBehaviors().contains(moveEndBehavior);
			}
		});
		enabledLabel.add(new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if (bottomMap.getBehaviors().contains(moveEndBehavior)) {
					bottomMap.remove(moveEndBehavior);
				} else {
					// TODO AbstractAjaxBehaviors are not reusable, so we have
					// to recreate:
					// https://issues.apache.org/jira/browse/WICKET-713
					moveEndBehavior = new MoveEndListener() {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onMoveEnd(AjaxRequestTarget target) {
							target.addComponent(center);
						}
					};
					bottomMap.add(moveEndBehavior);
				}
				target.addComponent(bottomMap);
				target.addComponent(enabledLabel);
			}
		});
		add(enabledLabel);

		Form geocodeForm = new Form("geocoder");
		add(geocodeForm);

		TextField addressTextField = new TextField("address", new Model(""));
		geocodeForm.add(addressTextField);
		geocodeForm.add(new GClientGeocoder("onsubmit", addressTextField,
				LOCALHOST) {
			@Override
			public void onGeoCode(AjaxRequestTarget target, int status,
					String address, GLatLng point) {
				if (status == GeocoderException.G_GEO_SUCCESS) {
					bottomMap.getInfoWindow().open(
							point,
							new GInfoWindowTab(address, new Label(address,
									address)));
				} else {
					error("Unable to geocode (" + status + ")");
					target.addComponent(feedback);
				}
			};
		});
		geocodeForm.add(new Button("submit"));
	}

	private void markerSelected(AjaxRequestTarget target, GMarker marker) {
		markerLabel.getModel().setObject(marker);
		target.addComponent(markerLabel);
	}

	/**
	 * pay attention at webapp deploy context, we need a different key for each
	 * deploy context check <a
	 * href="http://www.google.com/apis/maps/signup.html">Google Maps API - Sign
	 * Up</a> for more info. Also the GClientGeocoder is pickier on this than
	 * the GMap2. Running on 'localhost' GMap2 will ignore the key and the maps
	 * will show up, but GClientGeocoder wount. So if the key doesnt match the
	 * url down to the directory GClientGeocoder will not work.
	 * 
	 * This key is good for all URLs in this directory:
	 * http://localhost:8080/wicket-contrib-gmap2-examples/gmap/
	 */
	private static final String LOCALHOST = "ABQIAAAAzaZpf6nHOd9w1PfLaM9u2xSRJOeFm910afBJASoNgKJoF-fSURQRJ7dNBq-d-8hD7iUYeN2jQHZi8Q";
}