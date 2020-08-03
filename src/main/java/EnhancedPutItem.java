import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
// snippet-end:[dynamodb.java2.mapping.putitem.import]


/*
    Before running this code example, create a table named Customer with a PK named id
 */
public class EnhancedPutItem {

    public static void main(String[] args) {

        // Create a DynamoDbClient object


        String endpoint = "http://localhost:8000";

        DynamoDbClient ddb = DynamoDbClient.builder().endpointOverride(URI.create(endpoint)).build();

        // Create a DynamoDbEnhancedClient and use the DynamoDbClient object
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        putRecord(enhancedClient) ;
    }

    // snippet-start:[dynamodb.java2.mapping.putitem.main]
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
            custRecord.setRegistrationDate(instant) ;

            // Put the customer data into a DynamoDB table
            custTable.putItem(custRecord);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
//            System.exit(1);
        }
        System.out.println("done");
    }


    // Create the Customer table
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
    // snippet-end:[dynamodb.java2.mapping.putitem.main]
}
