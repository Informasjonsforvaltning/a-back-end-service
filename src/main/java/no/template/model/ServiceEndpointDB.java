package no.template.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import java.net.URI;
import java.net.URL;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="template")
public class ServiceEndpointDB {
    @Id private ObjectId id;
    @Indexed(unique = true) @NotBlank private String name;
    @Indexed(unique = true) @NotNull private URL uri;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUri() {
        return uri;
    }

    public void setUri(URL uri) {
        this.uri = uri;
    }
}
