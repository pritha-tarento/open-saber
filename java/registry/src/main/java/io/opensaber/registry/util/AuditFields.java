package io.opensaber.registry.util;

import io.opensaber.registry.service.RegistryAuditService;
import java.util.Date;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public enum AuditFields implements RegistryAuditService {

    createdOn {
        @Override
        public void ensureTimeStamp(Vertex vertex) {
            vertex.property("createdOn", new Date().toString());
            vertex.property("updatedOn", ""); // empty default value
        }
    },
    updatedOn {
        @Override
        public void ensureTimeStamp(Vertex vertex) {
            vertex.property("updatedOn", new Date().toString());
        }
    }

}
