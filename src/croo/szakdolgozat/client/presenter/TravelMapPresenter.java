package croo.szakdolgozat.client.presenter;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.web.bindery.event.shared.EventBus;

import croo.szakdolgozat.client.display.TravelMapDisplay;
import croo.szakdolgozat.client.events.PlaceRequestEvent;
import croo.szakdolgozat.client.events.PlaceRequestEventHandler;
import croo.szakdolgozat.client.events.SendEvent;
import croo.szakdolgozat.client.stubs.FilterServiceAsync;
import croo.szakdolgozat.client.stubs.MapServiceAsync;
import croo.szakdolgozat.client.stubs.callbacks.ErrorHandlingAsyncCallback;
import croo.szakdolgozat.shared.Route;

public class TravelMapPresenter implements PlaceRequestEventHandler
{
	private static final String MY_GOOGLEAPI_AUTH_KEY = "AIzaSyD--gmXsvTyag6v_Li5-wsYfdlXTMyauCU";
	private EventBus eventBus;
	private TravelMapDisplay display;
	private TravelMapManager mapManager;

	private final MapServiceAsync mapService;
	private final FilterServiceAsync filterService;

	public TravelMapPresenter(EventBus eventBus, TravelMapDisplay display, MapServiceAsync mapService,
			FilterServiceAsync filterService)
	{
		this.eventBus = eventBus;
		this.display = display;
		this.mapService = mapService;
		this.filterService = filterService;
		this.eventBus.addHandler(PlaceRequestEvent.TYPE, this);
	}

	public void verifyTownInput(final String location)
	{
		GWT.log("Validating of input " + location + ".");
		mapService.verifyLocation(location, new ErrorHandlingAsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean locationIsValid)
			{
				if (locationIsValid)
					display.setErrorLabel("");
				else
					display.setErrorLabel(location + " nevű város nincs az adatbázisban.");
			}
		});
	}

	public void onSendButtonClicked(String startTown, String destinationTown)
	{
		display.setErrorLabel("");

		GWT.log("Getting route information...");
		mapService.getRoute(startTown, destinationTown, new ErrorHandlingAsyncCallback<Route>() {
			@Override
			public void onSuccess(Route route)
			{
				if (route != null) {
					mapManager.drawRoute(route);
					eventBus.fireEvent(new SendEvent());
				} else {
					display.setErrorLabel("Nincs a két város között közvetlen járat.");
				}
			}
		});
	}

	public void initializeMap()
	{
		Maps.loadMapsApi(MY_GOOGLEAPI_AUTH_KEY, "2", false, getMapInitThread());
	}

	private Runnable getMapInitThread()
	{
		return new Runnable() {
			public void run()
			{
				mapManager = new TravelMapManager(new MapWidget(), eventBus);
				mapManager.initMap();
				display.setTravelMap(mapManager.getMap());
			}
		};
	}

	@Override
	public void onNewPlaceRequest(final PlaceRequestEvent event)
	{
		GWT.log("Sending a new place to database...");
		mapService.addNewInterestingPlace(event.getPlace(), new ErrorHandlingAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result)
			{
				GWT.log("The place successfully added to the database.");
				display.setErrorLabel("Az új helyet sikeresen elmentettük.");
				mapManager.updatePlacesListPanel(event.getPlace());
			}
		});
	}

	public void onDateBoxValueChange(Date date)
	{
		filterService.setDate(date, new ErrorHandlingAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result)
			{
				GWT.log("DateBox value succesfully changed on the server.");
			}

		});
	}

	public void onDiscountBoxChange(String rate)
	{
		filterService.setDiscountRate(rate, new ErrorHandlingAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result)
			{
				GWT.log("DiscountBox value succesfully changed on the server.");
			}
		});
	}

	public void loadDiscounts()
	{
		filterService.getDiscounts(new ErrorHandlingAsyncCallback<HashMap<String, String>>() {
			@Override
			public void onSuccess(HashMap<String, String> properties)
			{
				display.loadDiscountBoxData(properties);
			}
		});
	}
}
