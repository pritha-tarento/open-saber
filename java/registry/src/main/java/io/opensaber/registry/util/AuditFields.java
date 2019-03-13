package io.opensaber.registry.util;

import io.opensaber.registry.service.RegistryAuditService;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public enum AuditFields implements RegistryAuditService {

    createdOn {
        @Override
        public void ensureTimeStamp(Vertex vertex) {
            vertex.property("createdOn", getCurrentTimeStamp());
            vertex.property("updatedOn", ""); // empty default value
        }
    },
    updatedOn {
        @Override
        public void ensureTimeStamp(Vertex vertex) {
            vertex.property("updatedOn", getCurrentTimeStamp());
        }
    }

}
