package no.brreg.informasjonsforvaltning.abackendservice.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.net.URL;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="service-endpoints")
public class ServiceEndpointDB {

    @Id @Pattern(regexp = "[A-Za-z\\-]+") private String name;
    @Indexed(unique = true) @NotNull private URL url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL uri) {
        this.url = uri;
    }
}
