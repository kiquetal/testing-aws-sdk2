package domains

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean()
class Player {

    constructor() : this("", "", "")

    @get:DynamoDbPartitionKey
    lateinit var id: String

    var _dam:String=""
    get() = this.id


    @get:DynamoDbAttribute("name")
    lateinit var name: String

    @get:DynamoDbAttribute("attributes")
    lateinit var attributes: String;

    constructor(id: String, name: String, attributes: String) {
        this.id = id
        this.name = name
        this.attributes = attributes
    }

}


