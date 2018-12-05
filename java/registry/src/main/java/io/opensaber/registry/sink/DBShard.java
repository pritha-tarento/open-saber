package io.opensaber.registry.sink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.opensaber.registry.middleware.util.Constants;
import io.opensaber.registry.model.DBConnectionInfo;
import io.opensaber.registry.model.DatabaseProviders;

@Component("dbshard")
public class DBShard {
	
	@Autowired
	Environment environment;
	
	@Autowired
	DatabaseProviders databaseProviders;
	
	public DatabaseProvider getInstance(String name){
		String dbProvider = environment.getProperty(Constants.DATABASE_PROVIDER);
		DatabaseProvider provider;
		if (dbProvider.equalsIgnoreCase(Constants.GraphDatabaseProvider.ORIENTDB.getName())) {
			provider = new OrientDBGraphProvider(environment);
			provider.initializeGlobalGraphConfiguration();
		} else if (dbProvider.equalsIgnoreCase(Constants.GraphDatabaseProvider.NEO4J.getName())) {
			DBConnectionInfo connection = databaseProviders.getDatabaseConnection(name);
			if(connection == null)
				throw new RuntimeException("No shard is configured. Please configure a shard with "+name+" name.");
			provider = new Neo4jGraphProvider(connection);
		} else if (dbProvider.equalsIgnoreCase(Constants.GraphDatabaseProvider.SQLG.getName())) {
			provider = new SqlgProvider(environment);
			provider.initializeGlobalGraphConfiguration();
		} else if (dbProvider.equalsIgnoreCase(Constants.GraphDatabaseProvider.TINKERGRAPH.getName())) {
			provider = new TinkerGraphProvider(environment);
			provider.initializeGlobalGraphConfiguration();
		} else if (dbProvider.equalsIgnoreCase(Constants.GraphDatabaseProvider.CASSANDRA.getName())) {
			provider = new JanusGraphStorage(environment);
			provider.initializeGlobalGraphConfiguration();
		} else {
			throw new RuntimeException("No Database Provider is configured. Please configure a Database Provider");
		}
		return provider;

	}

}
