package io.opensaber.registry.service;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface RegistryAuditService {

//	public String frameAuditEntity(org.eclipse.rdf4j.model.Model entityModel) throws IOException;
//
//	public org.eclipse.rdf4j.model.Model getAuditNode(String id) throws IOException, NoSuchElementException,
//			RecordNotFoundException, EncryptionException, AuditFailedException;
//
//	public String getAuditNodeFramed(String id) throws IOException, NoSuchElementException, RecordNotFoundException,
//			EncryptionException, AuditFailedException, IOException, MultipleEntityException, EntityCreationException;
/*    
    default void createOn(Vertex vertex){
        vertex.property("createdOn", new Date());
        vertex.property("updatedOn", ""); // empty default value 
    }
    
    default void updateOn(Vertex vertex){
        vertex.property("updatedOn", new Date());
    }*/
    
    public void ensureTimeStamp(Vertex vertex);
    
}
