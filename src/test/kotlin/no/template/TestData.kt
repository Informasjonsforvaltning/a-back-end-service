package no.template

import com.google.common.collect.ImmutableMap
import no.template.generated.model.TemplateObject
import no.template.model.TemplateObjectDB
import org.bson.types.ObjectId

private const val MONGO_USER = "testuser"
private const val MONGO_PASSWORD = "testpassword"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"
const val MONGO_PORT = 27017
const val DATABASE_NAME = "templateAPI"

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD)

fun buildMongoURI(host: String, port: Int, withDbName: Boolean): String {
    var uri = "mongodb://$MONGO_USER:$MONGO_PASSWORD@$host:$port/"

    if (withDbName) {
        uri += DATABASE_NAME
    }

    return uri + MONGO_AUTH
}

val TEMPLATE_OBJECT_0 = TemplateObject().apply {
    id = "5d5531e55c404500068481da"
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
}

val TEMPLATE_OBJECT_1 =  TemplateObject().apply {
    id = "5d5531e45c40450006848160"
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
}

val TEMPLATE_OBJECT_2 =  TemplateObject().apply {
    id = "5d5531e45c40450006848159"
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
}

val NEW_TEMPLATE_OBJECT_0 =  TemplateObject().apply {
    name = "toBeUpdated0"
    uri = "uri0"
}

val NEW_TEMPLATE_OBJECT_1 =  TemplateObject().apply {
    name = "Name"
    uri = "uri1"
}

val UPDATE_TEMPLATE_OBJECT =  TemplateObject().apply {
    uri = "uriUpdated"
}

var TEMPLATE_OBJECTS = listOf(TEMPLATE_OBJECT_0, TEMPLATE_OBJECT_1, TEMPLATE_OBJECT_2)
var EMPTY_LIST = emptyList<TemplateObject>()

var TEMPLATE_OBJECT_DB_0 = TemplateObjectDB().apply {
    id = ObjectId("5d5531e55c404500068481da")
    name = "REGISTERENHETEN I BRØNNØYSUND"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/974760673"
}

val TEMPLATE_OBJECT_DB_1 =  TemplateObjectDB().apply {
    id = ObjectId("5d5531e45c40450006848160")
    name = "ATB AS"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/994686011"
}

val TEMPLATE_OBJECT_DB_2 =  TemplateObjectDB().apply {
    id = ObjectId("5d5531e45c40450006848159")
    name = "FORSVARET"
    uri = "http://data.brreg.no/enhetsregisteret/enhet/986105174"
}
