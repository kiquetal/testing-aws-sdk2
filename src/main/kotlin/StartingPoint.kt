import domains.Player
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI
import java.util.function.Consumer

/**
 * @author kiquetal@gmail.com
 * 2020-08-01T11:01:46.530Z

 */

fun main(args: Array<String>) {
    val region = Region.AP_SOUTH_1

    val dynamoDbClient = DynamoDbClient.builder().endpointOverride(URI.create("http://localhost:8000"))
            .region(region)
            .build()


    val tableBuilder: CreateTableRequest.Builder = CreateTableRequest.builder()
        .attributeDefinitions(AttributeDefinition.builder()
            .attributeType(ScalarAttributeType.S)
            .attributeName("ids")
            .build())
        .keySchema(KeySchemaElement.builder()
            .attributeName("ids")
            .keyType(KeyType.HASH).build())
        .provisionedThroughput { builder->

                builder.readCapacityUnits(4)
                builder.writeCapacityUnits(4)
            }
        .tableName("records");





    val player1 = Player(id = "hola", name = "kiquetal", attributes = "attributes");
    val ddbEnhancedClient by lazy { DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build()!! }
   // createTableFromBean(Player::class.java,"MiPlayer",ddbEnhancedClient)
    saveUser(ddbEnhancedClient,player1)
    saveUser(ddbEnhancedClient, Player("aaaaaaa","la","nada"))
   // obtainUser(ddbEnhancedClient,player1)
    print(player1._dam)
    scan(dynamoDbClient)

}
fun scan(db:DynamoDbClient){

    val scanResponse = db.scan(Consumer { t ->t.tableName("MiPlayer")
                t.limit(10)})

    scanResponse.items().map {
      print(" " + it.get("id") + "\n")
    }
}
fun obtainUser(db:DynamoDbEnhancedClient,player: Player)
{
    val custTable: DynamoDbTable<Player> = db.table("MiPlayer", TableSchema.fromBean(Player::class.java))

    val key = Key.builder()
            .partitionValue(player.id)
            .build()
    val p=custTable.getItem{ k-> k.key(key)}
    println(p.id + p.name);
}
fun saveUser(db: DynamoDbEnhancedClient,player: Player)
{

    // Create a DynamoDbTable object
    val custTable: DynamoDbTable<Player> = db.table("MiPlayer", TableSchema.fromBean(Player::class.java))

    custTable.putItem(player)
}

fun createTable(db: DynamoDbClient, builder: CreateTableRequest.Builder, tableName: String) {

    checkTable(db, tableName)


    try {
        val r = db.createTable(builder.build())
        print(r.tableDescription().tableName())
    } catch (ex: DynamoDbException) {
        ex.printStackTrace()
    }

}

fun checkTable(db: DynamoDbClient, tableName: String) {
    if (db.listTables().tableNames().contains(tableName)) {
        db.deleteTable(DeleteTableRequest.builder().tableName(tableName).build())
    }


}

fun createTableFromBean(any: Any , tableName: String, db: DynamoDbEnhancedClient) {
   return db.table(tableName, TableSchema.fromBean(Player::class.java)).createTable()


}
