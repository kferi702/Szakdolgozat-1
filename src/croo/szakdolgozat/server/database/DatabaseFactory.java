package croo.szakdolgozat.server.database;

import java.io.File;
import java.io.IOException;

import croo.szakdolgozat.server.SystemProperties;

public class DatabaseFactory
{

	private static final String RDF_DATABASE_FILE_PROPERTY_KEY = "rdf.database.file";

	public static Database createMockDatabase()
	{
		return new MockDatabase();
	}

	public static Database createRdfDatabase() throws IOException
	{
		String databaseFile = SystemProperties.GetInstance().get(RDF_DATABASE_FILE_PROPERTY_KEY);

		RdfDatabaseGenerator generator = new RdfDatabaseGenerator();
		File file = new File(databaseFile);
		if (!file.exists()) {
			generator.generateRdfDatabase(databaseFile);
		}

		return new RdfDatabase(databaseFile);
	}
}
