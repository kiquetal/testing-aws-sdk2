import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DemoDBEnhancedClient {

    public static void main(String[] args) {

        // Create the DynamoDbClient object
        Region region = Region.AP_SOUTH_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(region)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        createDynamoDBTable(enhancedClient);
        putRecord(enhancedClient);
        System.out.println(getItem(enhancedClient));
    }

    private static void createDynamoDBTable(DynamoDbEnhancedClient enhancedClient) {
        // enhancedClient.table("customer","Customer.class").createTable();

        enhancedClient.table("Customer", TableSchema.fromBean(Customer.class)).createTable();

    }

    // Put an item into a DynamoDB table
    public static void putRecord(DynamoDbEnhancedClient enhancedClient) {

        try {
            // Create a DynamoDbTable object
            DynamoDbTable<Customer> custTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));

            // Create an Instant object
            LocalDate localDate = LocalDate.parse("2020-04-07");
            LocalDateTime localDateTime = localDate.atStartOfDay();
            Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

            // Populate the table
            Customer custRecord = new Customer();
            custRecord.setCustName("Susan Blue");
            custRecord.setId("id103");
            custRecord.setEmail("sblue@noserver.com");
            custRecord.setRegistrationDate(instant);

            // Put the customer data into a DynamoDB table
            custTable.putItem(custRecord);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
         //   System.exit(1);
        }
        System.out.println("done");
    }

    public static String getItem(DynamoDbEnhancedClient enhancedClient) {
        Customer result = null;

        try {
            // Create a DynamoDbTable object
            DynamoDbTable<Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));

            // Create a KEY object
            Key key = Key.builder()
                    .partitionValue("id103")
                    .sortValue("Susan Blue")
                    .build();

            // Get the item by using the key
            result = mappedTable.getItem(r -> r.key(key));
            System.out.println("email " + result.getEmail());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
         //   System.exit(1);
        }
        return "The record id is " + result.getId();
        // testing git

    }
    @DynamoDbBean
    public static class Customer {

        private String id;
        private String name;
        private String email;
        private Instant regDate;

        @DynamoDbPartitionKey
        public String getId() {
            return this.id;
        };

        public void setId(String id) {

            this.id = id;
        }

        @DynamoDbSortKey
        public String getCustName() {
            return this.name;

        }

        public void setCustName(String name) {

            this.name = name;
        }

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {

            this.email = email;
        }

        public Instant getRegistrationDate() {
            return regDate;
        }
        public void setRegistrationDate(Instant registrationDate) {

            this.regDate = registrationDate;
        }
    }
}
