package croo.szakdolgozat.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import croo.szakdolgozat.shared.InterestingPlace;

public class InterestingPlaceTest
{

	private static final String NO_IMAGE_AVAILABLE = "http://www.findhomestaykorea.com/images/no_image.gif";
	private static final String URL_1 = "http://www.bla.hu";
	private static final String URL_2 = "http://www.foo.hu";

	@Test
	public void PlacesShouldBeEqualIfURLsAreEqual()
	{
		InterestingPlace place1 = new InterestingPlace(URL_1, "");
		InterestingPlace place2 = new InterestingPlace(URL_1, "Name", "Description", NO_IMAGE_AVAILABLE);
		assertEquals(place1, place2);
	}

	@Test
	public void PlacesShouldNotBeEqualIfURLsAreDifferent()
	{
		InterestingPlace place1 = new InterestingPlace(URL_2, "Name", "Description", NO_IMAGE_AVAILABLE);
		InterestingPlace place2 = new InterestingPlace(URL_1, "Name", "Description", NO_IMAGE_AVAILABLE);
		assertFalse(place1.equals(place2));
	}
}
